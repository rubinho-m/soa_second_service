package com.rubinho.ejb.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.rubinho.ejb.dto.AllVehiclePageableResponse;
import com.rubinho.ejb.dto.AllVehicles;
import com.rubinho.ejb.dto.Vehicle;
import com.rubinho.ejb.dto.VehicleType;
import com.rubinho.ejb.service.VehicleService;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.ejb3.annotation.Pool;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
@Pool("slsb-strict-max-pool")
public class VehicleServiceImpl implements VehicleService {
    private static final String API_BASE_URL = "https://%s:%d/api/v1";
    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String getAllVehiclesByNumberOfWheels(float from, float to) {
        try {
            final List<Vehicle> vehicles = getAllVehicles().stream()
                    .filter(vehicle -> mapNumberOfWheels(vehicle.getType()) >= from && mapNumberOfWheels(vehicle.getType()) <= to)
                    .collect(Collectors.toList());
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
                    new AllVehicles(vehicles)
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceUnavailableException("Could not get all vehicles by number of wheels");
        }
    }

    @Override
    public String getAllVehiclesByEnginePower(float from, float to) {
        try {
            final List<Vehicle> vehicles = getAllVehicles().stream()
                    .filter(vehicle -> vehicle.getEnginePower() >= from && vehicle.getEnginePower() <= to)
                    .collect(Collectors.toList());
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
                    new AllVehicles(vehicles)
            );
        } catch (Exception e) {
            throw new ServiceUnavailableException("Could not get all vehicles by engine power");
        }
    }

    private List<Vehicle> getAllVehicles() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, TRUST_ALL_CERTS, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new InternalServerErrorException("Couldn't create ssl context", e);
        }
        final Client client = ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier((hostname, session) -> true)
                .register(JacksonJsonProvider.class)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        try {
            final int port = Integer.parseInt(System.getenv().getOrDefault("FIRST_SERVICE_PORT", "2828"));
            final String host = System.getenv().getOrDefault("FIRST_SERVICE_HOST", "localhost");
            final String url = String.format(API_BASE_URL, host, port);
            try (Response response = client.target(url).path("/vehicles").request(MediaType.APPLICATION_JSON).get()) {
                if (response.getStatus() == 200) {
                    System.out.println("GOT 200");
                    try {
                        final AllVehiclePageableResponse pageableResponse = response.readEntity(AllVehiclePageableResponse.class);
                        return pageableResponse.getVehicles();
                    } catch (Exception e) {
                        System.out.println("НЕ СМОГЛИ РАСпАРСИТЬ");
                        throw e;
                    }
                }
                throw new ServiceUnavailableException("HTTP error code: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private int mapNumberOfWheels(VehicleType vehicleType) {
        if (VehicleType.CAR.equals(vehicleType)) {
            return 4;
        }
        if (VehicleType.MOTORCYCLE.equals(vehicleType)) {
            return 2;
        }
        if (VehicleType.CHOPPER.equals(vehicleType)) {
            return 3;
        }
        throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
    }
}

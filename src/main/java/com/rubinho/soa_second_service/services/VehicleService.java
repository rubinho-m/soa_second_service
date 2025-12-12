package com.rubinho.soa_second_service.services;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import com.rubinho.soa_second_service.dto.AllVehiclePageableResponse;
import com.rubinho.soa_second_service.dto.Vehicle;
import com.rubinho.soa_second_service.dto.VehicleType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class VehicleService {
    private static final String API_BASE_URL = "https://localhost:%d/api/v1";
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

    public List<Vehicle> getAllVehiclesByNumberOfWheels(float from, float to) {
        return getAllVehicles().stream()
                .filter(vehicle -> mapNumberOfWheels(vehicle.getType()) >= from && mapNumberOfWheels(vehicle.getType()) <= to)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getAllVehiclesByEnginePower(float from, float to) {
        return getAllVehicles().stream()
                .filter(vehicle -> vehicle.getEnginePower() >= from && vehicle.getEnginePower() <= to)
                .collect(Collectors.toList());
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
                .build();
        try {
            final int port = Integer.parseInt(System.getenv().getOrDefault("FIRST_SERVICE_PORT", "2828"));
            return client.target(String.format(API_BASE_URL, port))
                    .path("/vehicles")
                    .request(MediaType.APPLICATION_JSON)
                    .get(AllVehiclePageableResponse.class).getVehicles();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceUnavailableException(e.getMessage());
        } finally {
            client.close();
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

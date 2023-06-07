package ru.karaban;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ru.karaban.grpc.SimpleImageServiceGrpc;
import ru.karaban.grpc.TelemetryProto;
import ru.karaban.grpc.TelemetryServiceGrpc;

import javax.swing.*;
import java.util.Iterator;
public class Client {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext().build();
        JFrame window = new JFrame();
        JLabel screen = new JLabel();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);

        TelemetryServiceGrpc.TelemetryServiceBlockingStub telemetryStub
                = TelemetryServiceGrpc.newBlockingStub(channel);
        TelemetryProto.TelemetryRequest request = TelemetryProto.TelemetryRequest.newBuilder()
                .setPlace("default")
                .build();

        float pres = telemetryStub.getTelemetry(request).getPressure();
        float temp = telemetryStub.getTelemetry(request).getTemperature();
        float hum = telemetryStub.getTelemetry(request).getHumidity();

        System.out.printf("Temperature: %f \n Pressure: %f\n Humidity: %f%n%n", temp, pres, hum);

        SimpleImageServiceGrpc.SimpleImageServiceBlockingStub imageStub
                = SimpleImageServiceGrpc.newBlockingStub(channel);
        TelemetryProto.ImageRequest imageRequest= TelemetryProto.ImageRequest.newBuilder()
                .setUserName("User")
                .setCameraId(0)
                .build();
        Iterator<TelemetryProto.ImageResponse> response = imageStub.getImage(imageRequest);
        ImageIcon ic;

        while (response.hasNext()) {
            ByteString data = response.next().getData();
            ic = new ImageIcon(data.toByteArray());
            screen.setIcon(ic);
            window.setContentPane(screen);
            window.pack();
        }
        channel.shutdownNow();
    }
}

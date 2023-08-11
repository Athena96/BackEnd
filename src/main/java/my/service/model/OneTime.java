package my.service.model;

public record OneTime(
                String id,
                String simulationId,
                String title,
                Integer age,
                ChargeType chargeType,
                LineItem lineItem) {
}

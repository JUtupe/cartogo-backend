package pl.jutupe.cartogobackend.storage.domain.model

open class FileResource(
    val storagePath: String,
    val nameWithExtension: String,
) {

    val pathWithName: String
        get() = "$storagePath/$nameWithExtension"
}

class VehicleImageFileResource(
    rentalId: String,
    vehicleId: String,
    nameWithExtension: String
) : FileResource("rental/$rentalId/vehicle/$vehicleId", nameWithExtension)

class UserSignatureFileResource(
    userId: String,
    nameWithExtension: String
) : FileResource("user/$userId", nameWithExtension)

class DeliveryCustomerSignatureFileResource(
    rentalId: String,
    orderId: String,
    nameWithExtension: String
) : FileResource("rental/$rentalId/order/$orderId/delivery", nameWithExtension)
class ReceptionCustomerSignatureFileResource(
    rentalId: String,
    orderId: String,
    nameWithExtension: String
) : FileResource("rental/$rentalId/order/$orderId/reception", nameWithExtension)
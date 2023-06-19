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
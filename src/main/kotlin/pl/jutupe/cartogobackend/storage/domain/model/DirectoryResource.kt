package pl.jutupe.cartogobackend.storage.domain.model

open class DirectoryResource(
    val path: String
)

data class RentalDirectoryResource(
    val rentalId: String
) : DirectoryResource("rental/$rentalId")

data class VehicleDirectoryResource(
    val rentalId: String,
    val vehicleId: String
) : DirectoryResource("rental/$rentalId/vehicle/$vehicleId")
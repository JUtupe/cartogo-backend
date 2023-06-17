package pl.jutupe.cartogobackend.common.exceptions

class FileTypeNotSupportedException(extension: String, allowedExtensions: List<String>)
    : RuntimeException("File type $extension is not supported. Allowed extensions: $allowedExtensions")
package de.schreib.handball.handballtag.exceptions

class SpielNotFoundException(message: String?) : Exception(message)
class SpielAlreadyExistsException(message: String?) : Exception(message)
class MannschaftAlreadyPlaysException(message: String?) : Exception(message)
class MannschaftDoesNotExsistException(message: String?) : Exception(message)
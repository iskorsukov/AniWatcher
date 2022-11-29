package com.iskorsukov.aniwatcher.domain.exception

class RoomException(cause: Throwable): Exception(
    "Something went wrong when storing data", cause
)

class ApolloException(cause: Throwable): Exception(
    "Something went wrong when loading data",
    cause
)
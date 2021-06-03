package com.routesme.vehicles.helper

class ValidatorCodes {
    companion object {
        const val operationSuccess = "0000" //Operation Success
        const val deviceIsNotOpened = "1001" //Device is not opened
        const val parameterIsInvalid = "1002" //Parameter is invalid
        const val noCardOrCannotFindCard = "1003" //No card or can’t find card
        const val deviceIsWaitingForSwapMagstripe = "1101" //Device is waiting for swap magstripe
        const val stopSwappingMagstripe = "1102" //Stop swapping magstripe
        const val pinPad_Cancel_isPressed = "12A1" //Pin pad “Cancel” is pressed
        const val inputPinPadTimeOut = "12A2" //Input pin pad time out
        const val unknownError = "0001" //Unknown Error
    }
}
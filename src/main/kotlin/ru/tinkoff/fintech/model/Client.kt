/**
* 
* No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
*
* OpenAPI spec version: 1.0.0
* 
*
* NOTE: This class is auto generated by the swagger code generator program.
* https://github.com/swagger-api/swagger-codegen.git
* Do not edit the class manually.
*/
package ru.tinkoff.fintech.model


/**
 * 
 * @param birthDate 
 * @param cards 
 * @param firstName 
 * @param id 
 * @param lastName 
 * @param middleName 
 */
data class Client (
    val id: kotlin.String,
    val birthDate: java.time.LocalDateTime? = null,
    val cards: kotlin.Array<kotlin.String>? = null,
    val firstName: kotlin.String? = null,
    val lastName: kotlin.String? = null,
    val middleName: kotlin.String? = null
) {

}

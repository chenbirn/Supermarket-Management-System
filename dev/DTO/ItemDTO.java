package DTO;

import enums.DefectiveStatus;
import enums.location;

import java.time.LocalDate;
/**
 * Data Transfer Object for an individual Item.
 */
public record ItemDTO (
         int ItemId,
         double BuyPrice,
         LocalDate ExpirationDate,
         DefectiveStatus Status,
         location location,
         int productId
){
}

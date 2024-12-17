package crud.springboot.service;

import crud.springboot.model.Rental;
import java.util.List;

public interface RentalService {
    List<Rental> getAllRentals();
    Rental saveRental(Rental rental); 
    Rental getRentalById(long id);
    void deleteRentalById(long id);
    List<Rental> getRentalsByCustomerName(String customerName);
}
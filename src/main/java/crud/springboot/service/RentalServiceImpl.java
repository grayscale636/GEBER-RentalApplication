package crud.springboot.service;

import crud.springboot.model.Rental;
import crud.springboot.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Override
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Override
    public Rental saveRental(Rental rental) {
        return this.rentalRepository.save(rental);
    }

    @Override
    public Rental getRentalById(long id) {
        Optional<Rental> optional = rentalRepository.findById(id);
        Rental rental = null;
        if (optional.isPresent()) {
            rental = optional.get();
        } else {
            throw new RuntimeException("Rental not found for id :: " + id);
        }
        return rental;
    }

    @Override
    public void deleteRentalById(long id) {
        this.rentalRepository.deleteById(id);
    }

    @Override
    public List<Rental> getRentalsByCustomerName(String customerName) {
        return rentalRepository.findByCustomerName(customerName);
    }
}
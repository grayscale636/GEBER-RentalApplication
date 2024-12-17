package crud.springboot.service;

import crud.springboot.model.Vehicle;
import crud.springboot.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public void saveVehicle(Vehicle vehicle) {
        this.vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle getVehicleById(long id) {
        Optional<Vehicle> optional = vehicleRepository.findById(id);
        Vehicle vehicle = null;
        if (optional.isPresent()) {
            vehicle = optional.get();
        } else {
            throw new RuntimeException("Vehicle not found for id :: " + id);
        }
        return vehicle;
    }

    @Override
    public void deleteVehicleById(long id) {
        this.vehicleRepository.deleteById(id);
    }

    @Override
    public Page<Vehicle> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                   Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.vehicleRepository.findAll(pageable);
    }
}
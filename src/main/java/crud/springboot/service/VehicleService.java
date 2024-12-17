package crud.springboot.service;

import crud.springboot.model.Vehicle;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllVehicles();
    void saveVehicle(Vehicle vehicle);
    Vehicle getVehicleById(long id);
    void deleteVehicleById(long id);
    Page<Vehicle> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
package edu.eci.cvds.ECIBienestarGym.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.cvds.ECIBienestarGym.dto.ReservationDTO;
import edu.eci.cvds.ECIBienestarGym.enums.Status;
import edu.eci.cvds.ECIBienestarGym.exceptions.GYMException;
import edu.eci.cvds.ECIBienestarGym.model.ApiResponse;
import edu.eci.cvds.ECIBienestarGym.model.GymSession;
import edu.eci.cvds.ECIBienestarGym.model.Reservation;
import edu.eci.cvds.ECIBienestarGym.model.User;
import edu.eci.cvds.ECIBienestarGym.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Reservations", description = "Operaciones relacionadas con reservas del gimnasio")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Obtener todas las reservas", description = "Devuelve una lista con todas las reservas registradas.")
    @GetMapping("/trainer/reservations")

    public ResponseEntity<ApiResponse<List<Reservation>>> getAllReservations() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas obtenidas exitosamente", reservationService.getAllReservations()));
    }

    @Operation(summary = "Obtener reserva por ID", description = "Devuelve una reserva específica por su ID.")
    @GetMapping("/user/reservations/{id}")

    public ResponseEntity<ApiResponse<Reservation>> getReservationById(
            @Parameter(description = "ID de la reserva") @PathVariable String id) throws GYMException {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Reserva no encontrada", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva encontrada", reservation));
    }

    @Operation(summary = "Obtener reservas por ID de usuario", description = "Devuelve las reservas asociadas a un usuario.")
    @GetMapping("/user/reservations/user/{userId}")

    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByUserId(
            @Parameter(description = "ID del usuario") @PathVariable String userId) {
        User user = new User();
        user.setId(userId); // Crear un objeto User con el ID proporcionado
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas del usuario encontradas", reservationService.getReservationsByUserId(user)));
    }

    @Operation(summary = "Obtener reservas por sesión de gimnasio", description = "Devuelve las reservas asociadas a una sesión específica del gimnasio.")
    @GetMapping("/trainer/reservations/{sessionId}")

    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByGymSession(
            @Parameter(description = "ID de la sesión del gimnasio") @PathVariable String sessionId) {
        GymSession gymSession = new GymSession();
        gymSession.setId(sessionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas de la sesión encontradas", reservationService.getReservationsByGymSession(gymSession)));
    }

    @Operation(summary = "Obtener reservas por fecha de reserva", description = "Devuelve las reservas que coinciden con una fecha determinada.")
    @GetMapping("/trainer/reservations/date")

    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByReservationDate(
            @Parameter(description = "Fecha de la reserva en formato ISO") @RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime reservationDate) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas en la fecha especificada encontradas", reservationService.getReservationsByReservationDate(reservationDate)));
    }

    @Operation(summary = "Obtener reservas por estado", description = "Devuelve las reservas con un estado específico (por ejemplo: CONFIRMADA, CANCELADA).")
    @GetMapping("/trainer/reservations/status/{status}")

    public ResponseEntity<ApiResponse<List<Reservation>>> getReservationsByState(
            @Parameter(description = "Estado de la reserva") @PathVariable Status status) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservas encontradas por estado", reservationService.getReservationsByState(status)));
    }

    @Operation(summary = "Crear una nueva reserva", description = "Crea una nueva reserva en el sistema.")
    @PostMapping("/user/reservations")

    public ResponseEntity<ApiResponse<Reservation>> createReservation(
            @Parameter(description = "Detalles de la reserva") @RequestBody ReservationDTO reservation) {
        Reservation createdReservation = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Reserva creada exitosamente", createdReservation));
    }

    @Operation(summary = "Actualizar una reserva existente", description = "Actualiza los detalles de una reserva existente.")
    @PutMapping("/user/reservations/{id}")

    public ResponseEntity<ApiResponse<Reservation>> updateReservation(
            @Parameter(description = "ID de la reserva a actualizar") @PathVariable String id,
            @Parameter(description = "Detalles actualizados de la reserva") @RequestBody ReservationDTO reservation) throws GYMException {
        Reservation updatedReservation = reservationService.updateReservation(id, reservation);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reserva actualizada exitosamente", updatedReservation));
    }

    @Operation(summary = "Eliminar una reserva", description = "Elimina una reserva del sistema.")
    @DeleteMapping("/user/reservations/{id}")

    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @Parameter(description = "ID de la reserva a eliminar") @PathVariable String id) throws GYMException {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}

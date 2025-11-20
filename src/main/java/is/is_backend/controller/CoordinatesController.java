package is.is_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import is.is_backend.dto.coordinatesDto.CoordinatesPageRequestDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesPageResponseDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesRequestDTO;
import is.is_backend.dto.coordinatesDto.CoordinatesResponseDTO;
import is.is_backend.mapper.CoordinatesMapper;
import is.is_backend.models.Coordinates;
import is.is_backend.service.CoordinatesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/coordinates")
@AllArgsConstructor
@Tag(
        name = "Coordinates API",
        description = "API для управления координатами - создание, чтение, обновление и удаление координат")
public class CoordinatesController {

    private final CoordinatesService coordinatesService;

    @Operation(
            summary = "Создание новой координаты",
            description = "Создает новые координаты в системе и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Координаты успешно созданы",
                        content = @Content(schema = @Schema(implementation = CoordinatesResponseDTO.class)))
            })
    @PostMapping
    public ResponseEntity<CoordinatesResponseDTO> createCoordinates(
            @Parameter(description = "DTO для создания координаты") @Valid @RequestBody
                    CoordinatesRequestDTO coordinatesRequestDTO) {
        Coordinates coordinates = CoordinatesMapper.toEntity(coordinatesRequestDTO);
        Coordinates saved = coordinatesService.createCoordinate(coordinates);
        CoordinatesResponseDTO response = CoordinatesMapper.toResponseDTO(saved);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Обновление координаты",
            description = "Обновляет существующую координату по id и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Координаты успешно обновлена",
                        content = @Content(schema = @Schema(implementation = CoordinatesResponseDTO.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<CoordinatesResponseDTO> updaterCoordinates(
            @Parameter(description = "ID координаты", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "DTO для обновления координаты") @Valid @RequestBody
                    CoordinatesRequestDTO coordinatesRequestDTO) {
        Coordinates coordinates = CoordinatesMapper.toEntity(coordinatesRequestDTO);
        Coordinates updated = coordinatesService.updateCoordinate(id, coordinates);
        CoordinatesResponseDTO response = CoordinatesMapper.toResponseDTO(updated);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Удаление координаты",
            description = "Удаляет координаты по идентификатору и отправляет уведомление")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Координата успешно удалена",
                        content = @Content(schema = @Schema(implementation = CoordinatesResponseDTO.class)))
            })
    @DeleteMapping("/{id}")
    private ResponseEntity<CoordinatesResponseDTO> deleteCoordinates(
            @Parameter(description = "ID координат", required = true, example = "1") @PathVariable Long id) {
        Coordinates deleted = coordinatesService.deleteCoordinate(id);
        CoordinatesResponseDTO response = CoordinatesMapper.toResponseDTO(deleted);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Получение всех координат",
            description = "Возвращает пагинированный список всех координат с возможностью фильтрации")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Список координат успешно получен",
                        content = @Content(schema = @Schema(implementation = CoordinatesPageResponseDTO.class)))
            })
    @GetMapping
    public ResponseEntity<CoordinatesPageResponseDTO> getAllCoordinates(
            @Parameter(description = "Параметры пагинации и фильтрации") @ModelAttribute
                    CoordinatesPageRequestDTO pageRequest) {
        Page<Coordinates> coordinatesPage = coordinatesService.getAllCoordinates(pageRequest);
        Page<CoordinatesResponseDTO> dtoPage = coordinatesPage.map(CoordinatesMapper::toResponseDTO);
        CoordinatesPageResponseDTO response = CoordinatesPageResponseDTO.fromPage(dtoPage);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Получение координат по id", description = "Возвращает конкретные координаты по id")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Координата успешно найдена",
                        content = @Content(schema = @Schema(implementation = CoordinatesResponseDTO.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<CoordinatesResponseDTO> getCoordinates(
            @Parameter(description = "ID координат", required = true, example = "1") @PathVariable Long id) {
        Coordinates coordinates = coordinatesService.getCoordinates(id);
        CoordinatesResponseDTO response = CoordinatesMapper.toResponseDTO(coordinates);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

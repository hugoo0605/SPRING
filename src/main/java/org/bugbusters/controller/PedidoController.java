package org.bugbusters.controller;

import org.bugbusters.dto.PedidoDTO;
import org.bugbusters.dto.PedidoResponseDTO;
import org.bugbusters.entity.Pedido;
import org.bugbusters.entity.SesionMesa;
import org.bugbusters.repository.PedidoRepository;
import org.bugbusters.repository.SesionMesaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final SesionMesaRepository sesionMesaRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            SesionMesaRepository sesionMesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.sesionMesaRepository = sesionMesaRepository;
    }

    // Crear un nuevo pedido
    @PostMapping
    public Pedido crearPedido(@RequestBody PedidoDTO pedidoDTO) {
        // Obtener la sesión correspondiente al UUID
        Optional<SesionMesa> sesionMesaOpt = sesionMesaRepository.findById(pedidoDTO.getSesionId());
        if (sesionMesaOpt.isEmpty()) {
            throw new RuntimeException("Sesion no encontrada");
        }

        // Crear el objeto Pedido y asignar las relaciones
        Pedido pedido = new Pedido();
        pedido.setEstado(pedidoDTO.getEstado());
        pedido.setFechaCreacion(pedidoDTO.getFechaCreacion());
        pedido.setNotas(pedidoDTO.getNotas());
        pedido.setTotal(pedidoDTO.getTotal());
        pedido.setSesionMesa(sesionMesaOpt.get());

        // Guardar y devolver el pedido
        return pedidoRepository.save(pedido);
    }

    // Obtener todos los pedidos
    @GetMapping
    public List<PedidoResponseDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(PedidoResponseDTO::new)
                .collect(Collectors.toList());
    }


    // Obtener pedidos por estado
    @GetMapping("/estado/{estado}")
    public List<Pedido> listarPedidosPorEstado(@PathVariable String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    // Actualizar estado de un pedido
    @PutMapping("/{id}")
    public Pedido actualizarEstadoPedido(@PathVariable Long id, @RequestParam String estado) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedidoExistente.setEstado(estado);
        return pedidoRepository.save(pedidoExistente);
    }
}

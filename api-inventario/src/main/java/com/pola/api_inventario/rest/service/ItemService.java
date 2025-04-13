package com.pola.api_inventario.rest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pola.api_inventario.excepciones.CantidadInsuficienteException;
import com.pola.api_inventario.excepciones.ItemNotFoundException;
import com.pola.api_inventario.rest.controller.NotificationController;
import com.pola.api_inventario.rest.models.DescuentoDto;
import com.pola.api_inventario.rest.models.Item;
import com.pola.api_inventario.rest.models.ItemDto;
import com.pola.api_inventario.rest.models.Proveedor;
import com.pola.api_inventario.rest.repositorio.IproveedorDao;
import com.pola.api_inventario.rest.repositorio.ItemDao;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ItemService {

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private IproveedorDao proveedorDao;

    public void aplicarDescuento(DescuentoDto descuentoDto) {
        // Buscar el item por ID
        Optional<Item> optionalItem = itemDao.findById(descuentoDto.getIdItem());

        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException("El item con ID " + descuentoDto.getIdItem() + " no existe.");
        }

        Item item = optionalItem.get();

        if (item.getCantidadLotes() == null || item.getCantidadLotes() <= 0) {
            throw new CantidadInsuficienteException("La cantidad de lotes actual es 0 o no válida.");
        }
        if (item.getCantidadLotes() < descuentoDto.getCantidadLotes()) {

            throw new CantidadInsuficienteException("La cantidad de lotes a descontar excede la cantidad disponible.");
        }
        item.setCantidadLotes(item.getCantidadLotes() - descuentoDto.getCantidadLotes());

        if (item.getCantidadLotes() <= item.getCantidadMinimaLotes()) {
            notificationController.notificarCantidadBajaDeItem(item);
        }
        // Guardar los cambios en la base de datos
        itemDao.save(item);
    }

    @Transactional
    public Item guardarItem(ItemDto itemDto) {

        Proveedor proveedor = proveedorDao.findByNombreComercial(itemDto.getNombreComercial());
        if (proveedor == null) {
            throw new EntityNotFoundException(
                    "El proveedor con nombre '" + itemDto.getNombreComercial() + "' no esta registrado.");
        }

        Item itemGuardar = Item.builder()
                .pesoPorUnidad(itemDto.getPesoPorUnidad())
                .volumenPorUnidad(itemDto.getVolumenPorUnidad())
                .pesoLote(itemDto.getPesoLote())
                .cantidadLotes(itemDto.getCantidadLotes())
                .unidadesPorLote(itemDto.getUnidadesPorLote())
                .longitudPorUnidad(itemDto.getLongitudPorUnidad())
                .contactoProveedor(itemDto.getContactoProveedor())
                .fechaUltimaEntrada(itemDto.getFechaUltimaEntrada())
                .fechaUltimaSalida(itemDto.getFechaUltimaSalida())
                .caducidad(itemDto.getCaducidad())
                .categoria(itemDto.getCategoria())
                .nombre(itemDto.getNombre())
                .tempMax(itemDto.getTempMax())
                .tempMin(itemDto.getTempMin())
                .largo(itemDto.getLargo())
                .ancho(itemDto.getAncho())
                .altura(itemDto.getAltura())
                .esFragil(itemDto.isEsFragil())
                .estado(itemDto.getEstado())
                .ubicacion(itemDto.getUbicacion())
                .proveedor(proveedor)
                .cantidadMinimaLotes(itemDto.getCantidadMinimaLotes())
                .build();

        // notificar a los compradores
        notificationController.notificarNuevoItem(itemGuardar);
        return itemDao.save(itemGuardar);
    }

    public void eliminarItem(Long id) {

        itemDao.deleteById(id);
    }

    public Optional<Item> obtenerItemPorId(Long id) {
        return itemDao.findById(id);
    }
}
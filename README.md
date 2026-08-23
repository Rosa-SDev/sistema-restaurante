# Sistema de Gestion de Restaurante

Prototipo de aplicación de escritorio para la gestión de un restaurante: usuarios,
clientes, mesas, carta, pedidos, facturación y reservas.

Proyecto del primer corte de **Diseño de Soluciones** — Ingeniería de Sistemas.

## Tecnologías

- Java 21
- Swing (interfaz de escritorio)
- Arquitectura MVC por paquetes
- Datos en memoria (sin base de datos)

## Estructura

```
src/model/        entidades, enumeraciones e interfaces de negocio
src/controller/   lógica de aplicación y CRUD en memoria
src/view/         ventanas Swing
src/Main.java     punto de entrada
```

## Equipo

| Integrante |
|---|
| Rosa Isabel Peña Yagüe |
| Juan Sebastián Gallego Villamil |
| Daniel Esteban Guzmán Rodríguez |
| Daniel Felipe Vanegas Restrepo|

## Flujo de ramas

Usamos Git Flow adaptado.

| Rama | Proposito |
|------|-----------|
| `main` | Produccion. Solo versiones entregadas, cada una con su tag. |
| `develop` | Integracion y pruebas. Rama por defecto del repositorio. |
| `feature/N-tema` | Una tarea del tablero. Nace de `develop` y vuelve a `develop`. |
| `release/vX.Y` | Preparacion de una entrega. Nace de `develop`, va a `main` y a `develop`. |
| `hotfix/vX.Y.Z-tema` | Correccion urgente. Nace de `main`, va a `main` y a `develop`. |

### Convencion de commits

`feat`, `fix`, `docs`, `refactor`, `test`, `chore` seguidos del modulo:

```
feat(pedidos): agrega registro de pago dentro de Pedido
fix(mesas): corrige el estado al cancelar una reserva
```

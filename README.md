# Sistema de Gestión de Restaurante

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
src/model/        entidades, enumeraciones e interfaces de negocio  (18 clases)
src/controller/   lógica de aplicación, validación y CRUD en memoria (7 clases)
src/view/         ventanas Swing y componentes compartidos          (38 clases)
src/Main.java     punto de entrada: carga los datos de demostración y abre el login
```

Reglas de capa:

- `model` no importa Swing y no valida.
- `view` no manipula listas de dominio: siempre pasa por un controlador.
- `controller` no construye ventanas.

## Clases

### `model`

| Grupo | Clases |
|---|---|
| Usuarios | `Usuario`, `Administrador`, `Mesero`, `Cocinero`, `Cajero` |
| Dominio | `Cliente`, `Mesa`, `Platillo`, `Pedido`, `Factura`, `Reserva` |
| Enumeraciones | `EstadoMesa`, `EstadoPedido`, `EstadoReserva`, `MetodoPago` |
| Interfaces | `IActualizable`, `IDescontable` |
| Técnica | `Restaurante` |

El dinero es siempre `BigDecimal`. `Pedido.calcularTotal()` suma los platillos sin
impuestos; `Factura.emitir()` aplica el impuesto al consumo y congela el total.

### `controller`

`ControllerUsuario`, `ControllerCliente`, `ControllerMesa`, `ControllerPlatillo`,
`ControllerPedido`, `ControllerFactura`, `ControllerReserva`.

Cada uno mantiene su lista estática, valida las entradas lanzando
`RuntimeException` con mensaje en español, y hace de sujeto del Observer.

### `view`

| Grupo | Clases |
|---|---|
| Arranque | `GUILogin`, `GUIPrincipal`, `Sesion` |
| Compartidas | `ComponentesGUI`, `EstilosGUI`, `AdaptadorTablaModelo`, `Columna` |
| Usuarios | `GUIAgregarUsuario`, `GUIActualizarUsuario`, `GUIEliminarUsuario`, `GUIBuscarUsuario`, `GUIListarUsuarios` |
| Clientes | `GUIAgregarCliente`, `GUIActualizarCliente`, `GUIEliminarCliente`, `GUIBuscarCliente`, `GUIListarClientes` |
| Mesas | `GUIAgregarMesa`, `GUIActualizarMesa`, `GUIEliminarMesa`, `GUIBuscarMesa`, `GUIListarMesas` |
| Carta | `GUIAgregarPlatillo`, `GUIActualizarPlatillo`, `GUIEliminarPlatillo`, `GUIBuscarPlatillo`, `GUIListarPlatillos` |
| Pedidos | `GUICrearPedido`, `GUIGestionarPedido`, `GUIFacturarPedido`, `GUIBuscarPedido`, `GUIListarPedidos`, `GUIListarFacturas` |
| Reservas | `GUIRegistrarReserva`, `GUIGestionarReserva`, `GUIBuscarReserva`, `GUIListarReservas` |
| Operaciones | `GUICalculos` |

## Patrones de diseño

| Patrón | Dónde |
|---|---|
| Singleton | `Restaurante.getInstancia()` |
| Observer | `IActualizable` + los controladores como sujeto; las ventanas de listado como observadoras |
| Adapter | `AdaptadorTablaModelo<T>` adapta `List<T>` del dominio al `TableModel` que Swing exige |
| MVC | Los tres paquetes |

## Cómo ejecutar

Requiere **JDK 21**.

### Desde IntelliJ IDEA

1. Abrir la carpeta del proyecto.
2. Clic derecho sobre `src` → *Mark Directory as* → *Sources Root*.
3. Abrir `src/Main.java` y ejecutar.

### Desde la línea de comandos (PowerShell)

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp bin Main
```

El proyecto no declara la codificación en ningún archivo de configuración, así que
`-encoding UTF-8` es obligatorio: sin él las tildes de la interfaz se corrompen.

## Datos de demostración

Los datos viven en memoria y se pierden al cerrar la aplicación. `Main` carga un
usuario por cada rol, 2 clientes, 4 mesas y 6 platillos.

| Correo | Contraseña | Rol |
|---|---|---|
| `admin@restaurante.com` | `admin123` | Administrador |
| `mesero@restaurante.com` | `mesero123` | Mesero |
| `cocinero@restaurante.com` | `cocinero123` | Cocinero |
| `cajero@restaurante.com` | `cajero123` | Cajero |

Las contraseñas no se guardan en claro: `Usuario` las convierte a SHA-256 en el
constructor.

La mesa 3 arranca en `RESERVADA` y el platillo «Mojarra frita» arranca agotado,
para poder mostrar los estados y el rechazo del controlador.

## Roles y permisos

`GUIPrincipal` habilita los menús según el rol conectado. Se parte de todo
deshabilitado y se enciende solo lo que corresponde.

| Rol | Usuarios | Clientes | Mesas | Carta | Pedidos | Reservas | Operaciones |
|---|---|---|---|---|---|---|---|
| Administrador | todo | todo | todo | todo | todo | todo | sí |
| Mesero | — | todo | Buscar, Listar | Listar | Crear, Gestionar, Buscar, Listar | todo | — |
| Cocinero | — | — | — | Listar | Buscar, Listar | — | — |
| Cajero | — | Buscar, Listar | — | Listar | Facturar, Buscar, Listar, Listar facturas | — | sí |

## Equipo

| Integrante | Responsabilidad principal |
|---|---|
| Rosa Isabel Peña Yagüe | Modelo del flujo de pedido, facturación e integración |
| Juan Sebastián Gallego Villamil | Jerarquía de usuarios, clases técnicas y vistas de reservas |
| Daniel Esteban Guzmán Rodríguez | Componentes de vista, patrón Adapter y autenticación |
| Daniel Felipe Vanegas Restrepo | Controladores y vistas de clientes y carta |

## Flujo de ramas

Usamos Git Flow adaptado.

| Rama | Propósito |
|------|-----------|
| `main` | Producción. Solo versiones entregadas, cada una con su tag. |
| `develop` | Integración y pruebas. Rama por defecto del repositorio. |
| `feature/tema` | Una tarea concreta. Nace de `develop` y vuelve a `develop`. |
| `release/vX.Y` | Preparación de una entrega. Nace de `develop`, va a `main` y a `develop`. |
| `hotfix/vX.Y.Z-tema` | Corrección urgente. Nace de `main`, va a `main` y a `develop`. |

La rama se publica en cuanto nace y cada commit se sube en cuanto se cierra.
`main` y `develop` están protegidas: todo entra por Pull Request.

### Convención de commits

`feat`, `fix`, `docs`, `refactor`, `test`, `chore` seguidos del módulo:

```
feat(pedidos): agrega registro de pago dentro de Pedido
fix(mesas): corrige el estado al cancelar una reserva
```

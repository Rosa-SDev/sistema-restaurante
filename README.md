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
src/model/        entidades, enumeraciones e interfaces de negocio
src/controller/   lógica de aplicación y CRUD en memoria
src/view/         ventanas Swing
src/Main.java     punto de entrada
```

## Cómo ejecutar

Requiere **JDK 21**.

1. Abrir la carpeta del proyecto en IntelliJ IDEA.
2. Clic derecho sobre `src` → *Mark Directory as* → *Sources Root*.
3. Abrir `src/Main.java` y ejecutar.

## Equipo

| Integrante | Responsabilidad principal |
|---|---|
| Rosa Isabel Peña Yagüe | Modelo del flujo de pedido, facturación e integración |
| Juan Sebastián Gallego Villamil | Jerarquía de usuarios y clases técnicas |
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

### Convención de commits

`feat`, `fix`, `docs`, `refactor`, `test`, `chore` seguidos del módulo:

```
feat(pedidos): agrega registro de pago dentro de Pedido
fix(mesas): corrige el estado al cancelar una reserva
```

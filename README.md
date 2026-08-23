# Sistema de Gestion de Restaurante

Prototipo de aplicacion de escritorio para la gestion de un restaurante: usuarios,
clientes, mesas, carta, pedidos, facturacion y reservas.

Proyecto del primer corte de **Diseno de Soluciones** — Ingenieria de Sistemas.

## Tecnologias

- Java 21
- Swing (interfaz de escritorio)
- Arquitectura MVC por paquetes
- Datos en memoria (sin base de datos en este corte)

## Estructura

```
src/model/        entidades, enumeraciones e interfaces de negocio
src/controller/   logica de aplicacion y CRUD en memoria
src/view/         ventanas Swing
src/Main.java     punto de entrada
```

## Compilar y ejecutar

Desde la raiz del proyecto, en PowerShell:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp bin Main
```

En IntelliJ IDEA: abrir la carpeta como proyecto, marcar `src` como *Sources Root*
y ejecutar `Main`.

## Equipo

| Integrante | Responsabilidad principal |
|---|---|
| Rosa | Modelo del flujo de pedido, facturacion e integracion |
| Sebastian | Jerarquia de usuarios y capa de controladores |
| Daniel | Componentes de vista y patron Adapter |
| Danielito | Carta, reservas y vistas del flujo de pedido |

## Flujo de ramas

Usamos Git Flow adaptado.

| Rama | Proposito |
|------|-----------|
| `main` | Produccion. Solo versiones entregadas, cada una con su tag. |
| `develop` | Integracion y pruebas. Rama por defecto del repositorio. |
| `feature/N-tema` | Una tarea del tablero. Nace de `develop` y vuelve a `develop`. |
| `release/vX.Y` | Preparacion de una entrega. Nace de `develop`, va a `main` y a `develop`. |
| `hotfix/vX.Y.Z-tema` | Correccion urgente. Nace de `main`, va a `main` y a `develop`. |

### Reglas

1. Nadie hace push directo a `main` ni a `develop`.
2. Todo entra por Pull Request con minimo una aprobacion.
3. Antes de subir: `git merge develop` en tu rama y resolver conflictos en local.
4. La rama se borra al fusionar el PR.
5. Cada entrega se etiqueta con un tag (`v1.0`, `v2.0`).

### Convencion de commits

`feat`, `fix`, `docs`, `refactor`, `test`, `chore` seguidos del modulo:

```
feat(pedidos): agrega registro de pago dentro de Pedido
fix(mesas): corrige el estado al cancelar una reserva
```

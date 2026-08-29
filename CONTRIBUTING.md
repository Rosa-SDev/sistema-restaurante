# Cómo contribuir

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

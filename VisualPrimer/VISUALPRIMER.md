<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: VISUALPRIMER.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# VisualPrimer

Second Gradle build in this repository. License: `LICENSE` in this directory, the same MIT text as the repository root, Copyright (c) 2026 VixoTeam Systems. `RIGHTS.txt` does not replace that notice.

Two Fabric client mods and shared math with no Minecraft imports. Mod id `visualprimer`, packages `visual.primer.mc121`, `visual.primer.mc26`, `visual.primer.shared`. Entrypoint `PrimerClient`. The root mod id is `vixoteam`, packages `team.vixo.*`. Same caps and the same depth-test split. The default palette below is the difference in the settings object.

Палитра одна на всё дерево, её читают в кадре, а не копируют в вершину в момент включения:

| Поле | ARGB | Смысл |
| --- | --- | --- |
| tracer | `0xE8E6E0D4` | линия к игроку, бумага |
| box | `0xC08C5A42` | 12 рёбер, глина |
| hat | `0xB0455C48` | конус, хвоя |
| circle | `0xFFC4A574` | кольцо прыжка, песок |
| trail (26.x) | `0xFFD8C7A8` | след на 26.2 |

Размытие по умолчанию выключено. Остальные тумблеры включены.

## Как собрать

From this directory (`VisualPrimer/`):

```bat
gradlew.bat :1.21.x:build
gradlew.bat :26.x:build
```

Yarn `1.21.11+build.6`, Java 21, Loom `1.17.17`, loader `0.19.3`, Fabric API `0.141.6+1.21.11`. Готовый jar: `1.21.x/build/libs/visualprimer-1.21.11-1.0.0.jar`.

Ветка `26.x` — Mojmap `26.2`, Java 25, Loom `1.17.21` (id плагина `net.fabricmc.fabric-loom`). Сборка этой ветки требует JDK 25. Имена методов между ветками не совпадают: строка, верная на Yarn, на Mojmap отсутствует. Запущенный клиент — «не проверено».

Общая библиотека `:shared_render` подключается и `implementation`, и `include`. Её ресурсы шейдеров добавлены в `sourceSets` обоих модов.

## Кадр

1. `GameRenderer.renderWorld` на `TAIL` зовёт `WorldRenderBus` с `RenderTickCounter.getTickProgress(true)` и `FrameClock.seconds()`.
2. Слушатели рисуют линии игроков, кольца, конус, след и размытие.
3. `InGameHud.render` на `TAIL` зовёт `HudRenderBus` с `DrawContext`.
4. `HeldItemRenderer.renderFirstPersonItem` на `HEAD` сдвигает позу руки.
5. `ClientPlayerEntity.tickMovement` на `TAIL` замечает прыжок.

`tickDelta` — пила 0..1 между тиками. `FrameClock.seconds()` — секунды с прошлого мирового хука. Если промежуток отрицательный или больше 0.1 с, часы возвращают `0.016`. Делить `tickDelta` на 20 и называть это длительностью кадра нельзя: кольцо, которое стареет от `tickDelta`, замирает, когда частичный тик стоит на месте.

На 26.2 мировой хук — `LevelRenderer.render` на `HEAD`, затем `collectPerFrameRenderThreadGizmos`. HUD — `Hud.extractRenderState`. Руки — `ItemInHandRenderer.submitHandsWithItems` на `HEAD` (один `PoseStack` на обе руки). Прыжок — `LivingEntity.jumpFromGround`. Возраст кольца — `DeltaTracker.getGameTimeDeltaTicks()`.

## Вход

`fabric.mod.json`: `"environment": "client"`, entrypoint `visual.primer.mc121.PrimerClient`. В `onInitializeClient` только регистрация слушателей. Текстуру, шейдер и framebuffer там не создают: контекста GL ещё нет, а перезагрузка ресурсов оставит объект без владельца. Шейдеры размытия грузятся в первом включённом мировом проходе.

```java
public final class PrimerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EspTracersModule.init();
        JumpCirclesModule.init();
        ChinaHatModule.init();
        TrailModule.init();
        KawaseBlurModule.init();
        TargetHudModule.init();
        ViewModelModule.init();
    }
}
```

Миксины только `@Inject`. `@Overwrite` нет. Имя метода — mapped-имя этой версии, Loom сам ремапит. Строку `method_` не писать. Список параметров копировать с уже собирающегося хука.

```java
@Mixin(GameRenderer.class)
public abstract class GameRendererWorldRenderMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void primer$afterWorld(RenderTickCounter counter, CallbackInfo ci) {
        WorldRenderBus.dispatch(counter.getTickProgress(true), FrameClock.seconds());
    }
}
```

Прыжок: был на земле, сейчас нет, `getVelocity().y > 0`. Это не инжект в метод `jump`.

```java
@Inject(method = "tickMovement", at = @At("TAIL"))
private void primer$detectJump(CallbackInfo ci) {
    ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
    boolean ground = self.isOnGround();
    if (primer$wasOnGround && !ground && self.getVelocity().y > 0.0) {
        JumpCirclesModule.onPlayerJump(self.getX(), self.getY(), self.getZ());
    }
    primer$wasOnGround = ground;
}
```

Рука. Вторая рука зеркалит X и рыскание. Если `viewOffhand` выключен, вторая рука не трогается.

```java
public static void apply(MatrixStack matrices, Hand hand) {
    VisualSettings s = VisualSettings.SHARED;
    if (hand == Hand.OFF_HAND && !s.viewOffhand()) {
        return;
    }
    float sign = hand == Hand.MAIN_HAND ? 1.0f : -1.0f;
    matrices.translate(s.viewX() * sign, s.viewY(), s.viewZ());
    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(s.viewRotX()));
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(s.viewRotY() * sign));
    float scale = s.viewScale();
    matrices.scale(scale, scale, scale);
}
```

## Шины и часы

`WorldRenderBus` — список `(tickDelta, frameDt)`. `HudRenderBus` — список `HudPass.render(DrawContext, frameDt)`. Один общий `VisualSettings.SHARED`.

Мировая геометрия идёт в мировую шину и в координатах камеры: из позиции вычитается `Camera.getCameraPos()`, тело берётся через `getLerpedPos(tickDelta)`. Текст и лицо — в HUD-шину и `DrawContext`. Линия из `InGameHud.render` получит матрицу GUI, а не камеры. `DrawContext.fill` из `renderWorld` не является обводкой блока.

`MatrixStack` на кадр не выделять. `WorldMatrices.identity()` один на всех. Кто сделал `push`, обязан сделать `pop`.

Состояние GL читается и пишется через `GL11`, потому что в этом Yarn нет `RenderSystem.enableBlend`, `depthMask`, `ShaderProgramKeys` и `BufferRenderer.drawWithGlobalProgram`. `GL11` не обновляет кэш состояния Minecraft. Перед возвратом слушателя вернуть depth, depth mask, cull, blend, framebuffer, viewport, текстуру и программу.

```java
public static void forOverlay(Runnable draw) {
    drawCaptured(draw, false);
}

public static void forTranslucentWorld(Runnable draw) {
    drawCaptured(draw, true);
}
```

`forOverlay` выключает тест глубины: линия не прячется за блоки. Им пользуются линии к игрокам. `forTranslucentWorld` оставляет глубину включённой: конус, след, кольца. Оба пути ставят blend `SRC_ALPHA / ONE_MINUS_SRC_ALPHA`, выключают cull и маску глубины, в `finally` пишут обратно четыре бита, которые прочитал `capture()`.

Путь вершин, который совпадает с этим Loom:

`Tessellator.getInstance().begin(VertexFormat.DrawMode, VertexFormat)`, затем `buffer.end()`, затем `RenderLayers.LINES.draw` или `RenderLayers.debugQuads().draw`. `VertexFormat` лежит в `com.mojang.blaze3d.vertex.VertexFormat`. `RenderLayers.DEBUG_QUADS` приватный, публичный путь — `debugQuads()`. Линии — `VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH`. Нормаль — единичное направление отрезка из `MathUtility.direction`. Если длина почти ноль, в буфер пишется `(0, 1, 0)` и отрезок выше по стеку пропускается.

## Модули 1.21.11

| Класс | Буфер | Что рисует |
| --- | --- | --- |
| `EspTracersModule` | 48 игроков, 13 отрезков | 12 рёбер бокса и одна линия от камеры к центру тела. Чужие игроки, не локальный. Конус обзора отсекает тех, кто вне экрана. `forOverlay`. |
| `JumpCirclesModule` | 32 слота | Прыжок пишет x, y+0.015, z. Квад в плоскости XZ, радиус `circleRadius * (0.3 + t)`, альфа `(1 - t)`. Жизнь 0.9 с по `frameDt`. Смена мира очищает слоты. `debugQuads()`. |
| `ChinaHatModule` | 24 дольки | Ребро конуса и ребро обода на голову. Глубина включена. |
| `TrailModule` | 24 точки ног | Новая точка, если сдвиг больше 0.2 блока (`dist2 > 0.04`). Сброс: модуль выключен, игрок мёртв, другой `ClientWorld`, разрыв больше 64 блоков. |
| `KawaseBlurModule` | ping-pong половинного размера | Вниз-вверх-вниз-вверх, финальный upsample в framebuffer, который был связан в начале прохода. Шейдеры ленивые, `shutdown` их снимает. |
| `TargetHudModule` | панель прицела | Скруглённый SDF, лицо из скина таба, здоровье с экспоненциальным сглаживанием. Забывает сущность через 3 с вне прицела. |
| `ViewModelModule` | поза руки | Сдвиг, рыскание, один масштаб. |

Конус обзора линий: вертикальный угол — `options.getFov()` умножить на `AbstractClientPlayerEntity.getFovMultiplier(true, tickDelta)` у игрока в фокусе, затем масштаб воды или лавы `lerp(fovEffectScale, 1, 0.85714287)`. К обоим полууглам до `tan` добавляется `atan2(радиус сферы, дистанция)`.

Бокс — bounding box, сдвинутый на `lerped - getX/getY/getZ`. Координаты в буфере уже относительно камеры.

Кольцо не крутят на 90° вокруг X: это земля, не билборд. Статический список, переживший `onInitializeClient`, нормален. Статический список, переживший смену сервера, — ошибка.

Панель цели. Сущность — `MinecraftClient.targetedEntity`. Скин: `getNetworkHandler().getPlayerListEntry(uuid).getSkinTextures().body().texturePath()`. `getPlayerListEntry()` на сущности защищён. Рисовать через `RenderPipelines.GUI_TEXTURED` из `net.minecraft.client.gl`. Лицо: `drawTexture(pipeline, id, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight)`. Скругление — шейдер `round_rect`, не цикл точек и не картинка AWT.

Размытие. Даунсэмпл берёт половину текселя, не целый тексель за пикселем. Вершинный шейдер пишет clip-space `Position` и не умножает на `ProjMat`. Цвет — `GpuTexture`; `GlTexture.getGlId()` только если вложение действительно `GlTexture`. У `Framebuffer` нет публичного GL id, поэтому каждый включённый проход читает связанный framebuffer, viewport, текстуру и программу и восстанавливает их в `finally`. Четвёртый квад снова биндит сохранённый framebuffer и поднимает `PING` на него. Результат не остаётся в `PONG`. Квад в clip-space имеет `z = 0`, поэтому на четыре прохода тест глубины выключен, иначе буфер глубины мира отбросит композит. Трафик размытия — локальная видеопамять.

## Модули 26.2

| Класс | Работа |
| --- | --- |
| `PrimerClient` | Вызывает `JumpMarks.bind()`. |
| `WorldGizmos.emit` | Внутри `collectPerFrameRenderThreadGizmos`: кубоид блока под прицелом, до 48 боксов и линий в радиусе 64, конус из 24 долек, след из 24 `Vec3`. |
| `JumpMarks` | 32 кольца, возраст в игровых дельта-тиках, исчезают после 18, `Gizmos.circle` на 20 сегментов. |
| `TargetPanel` | Полоска HUD, когда под прицелом живая сущность. |
| `TrailSettings` | Тумблер и цвет следа. |
| `LevelRendererGizmoMixin` | `LevelRenderer.render` HEAD → `WorldGizmos.emit` до слива гизмо. |

`Gizmos.line` держит ссылку на `Vec3` до выпуска. Поля `Vec3` финальные, поэтому кольцо следа хранит экземпляр, созданный вместе с точкой. Новый `Vec3` на каждый отрезок каждого кадра не выделять.

Один кубоид — двенадцать рёбер. Обводка блока — этот кубоид, не облако точек и не экранный min/max восьми углов.

## Общая математика

Пакет `visual.primer.shared` не импортирует Minecraft. Поэтому на него опираются обе версии.

`MathUtility.direction` пишет единичный вектор отрезка в переиспользуемый `float[3]` и возвращает false на вырожденном отрезке.

`ColorUtility` достаёт каналы ARGB и умножает альфу.

`Interpolation.exponential`: `current + (target - current) * (1 - exp(-rate * dt))`. Этим сглаживается полоска здоровья. `spring` — полунеявная пружина, критическое затухание только при `damping = 2 * sqrt(stiffness)`. `cubicBezier1D` — полином через четыре значения, это не CSS-тайминг.

## Шейдеры

Лежат в `shared_render/src/main/resources/assets/visualprimer/shaders/`. Читает их `ShaderSources` через `Identifier.of("visualprimer", path)`.

`screen_quad.vsh` кладёт `Position` прямо в `gl_Position`. `kawase_down.fsh` усредняет четыре выборки со сдвигом в половину текселя. `kawase_up.fsh` собирает кольцо вокруг текселя. `round_rect.fsh`:

```glsl
float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    vec2 q = abs(p) - halfSize + radius;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;
}
```

Альфа края — `smoothstep` по `fwidth(dist)`.

## Куда класть новую картинку

Регистрировать слушатель из `PrimerClient`. Дать ему поле в `VisualSettings`, фиксированный буфер и очистку при смене мира, если он хранит точки. Мировая линия — мировая шина. Лицо и текст — HUD-шина. Ползунок, который ни разу не попал в вершину или в uniform, функцией не является.

Зелёная компиляция — не запущенная игра. После правки снова `gradlew.bat :1.21.x:build` и смотреть новый лог.

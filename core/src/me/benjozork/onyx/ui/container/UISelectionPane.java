package me.benjozork.onyx.ui.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;

import me.benjozork.onyx.GameManager;
import me.benjozork.onyx.object.TextComponent;
import me.benjozork.onyx.utils.Utils;

/**
 * @author Benjozork
 */
public class UISelectionPane extends UIPane {

    public Color selectedPaneBackgroundColor = Utils.rgba(160, 160, 160, 200);
    public Color selectorBackgroundColorUnselected = Utils.rgba(130, 130, 130, 200);
    public Color selectorBackgroundColorSelected = Utils.rgba(160, 160, 160, 110);

    public float selectorItemWidth = 80;

    private float SELECTOR_ITEM_HEIGHT;

    private ArrayMap<TextComponent, UIPane> selectiorItems = new ArrayMap<TextComponent, UIPane>();
    private int currentPane = 0;

    public UISelectionPane(float x, float y, float w, float h, UIContainer parent) {
        super(x, y, w, h, parent);
    }

    public void addSelectorItem(TextComponent item, UIPane pane) {
        pane.setRelativeX(selectorItemWidth);
        pane.setRelativeY(0);
        pane.setWidth(getWidth() - selectorItemWidth);
        pane.setHeight(getHeight());
        addChild(pane);
        selectiorItems.put(item, pane);
        SELECTOR_ITEM_HEIGHT = getHeight() / selectiorItems.size;
    }

    @Override
    public void update() {
        if (Gdx.input.justTouched()) {
            click();
        }

        selectiorItems.getValueAt(currentPane).update();
    }

    @Override
    public void draw() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        GameManager.setIsShapeRendering(true);
        GameManager.getRenderer().set(ShapeRenderer.ShapeType.Filled);
        GameManager.getRenderer().setColor(selectedPaneBackgroundColor);
        GameManager.getRenderer().rect(getAbsoluteX() + selectorItemWidth, getAbsoluteY(), getWidth() - selectorItemWidth, getHeight());
        GameManager.setIsShapeRendering(false);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        for (TextComponent component : selectiorItems.keys()) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            GameManager.setIsShapeRendering(true);
            GameManager.getRenderer().set(ShapeRenderer.ShapeType.Filled);
            if (selectiorItems.indexOfKey(component) != currentPane) {
                GameManager.getRenderer().setColor(selectorBackgroundColorUnselected);
            } else GameManager.getRenderer().setColor(selectorBackgroundColorSelected);

            GameManager.getRenderer().rect(getAbsoluteX(), getAbsoluteY() + getHeight() - (SELECTOR_ITEM_HEIGHT * (selectiorItems.indexOfKey(component) + 1)), selectorItemWidth, SELECTOR_ITEM_HEIGHT);
            GameManager.setIsShapeRendering(false);
            Gdx.gl.glDisable(GL20.GL_BLEND);
            component.drawCenteredInContainer(GameManager.getBatch(), getAbsoluteX(), getAbsoluteY() + getHeight() - ((selectiorItems.indexOfKey(component) + 1) * SELECTOR_ITEM_HEIGHT), selectorItemWidth, SELECTOR_ITEM_HEIGHT);
        }

        selectiorItems.getValueAt(currentPane).draw();
    }

    @Override
    public boolean click() {
        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 unprojected = Utils.unprojectWorld(mouse);

        if (unprojected.x > getAbsoluteX()
            && unprojected.x < getAbsoluteX() + selectorItemWidth
            && unprojected.y > getAbsoluteY()
            && unprojected.y < getAbsoluteY() + getHeight()) {
            float dy = getAbsoluteY() + getHeight() - unprojected.y;
            currentPane = (int) (dy / SELECTOR_ITEM_HEIGHT);
            return true;
        }
        return false;
    }
}
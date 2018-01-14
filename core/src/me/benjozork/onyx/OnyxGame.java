package me.benjozork.onyx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import me.benjozork.onyx.config.Configs;
import me.benjozork.onyx.config.ProjectConfig;
import me.benjozork.onyx.console.Console;
import me.benjozork.onyx.logger.Log;
import me.benjozork.onyx.object.TextComponent;
import me.benjozork.onyx.utils.Utils;

/**
 * The main Onyx client
 *
 * @version 0.6.0-alpha
 *
 * Written with <3 by :
 *
 * @author Benjozork
 * @author angelickite
 * @author RishiRaj22
 *
 * To whoever this may read this code, we wish you all happiness in the world !
 *      ;)
 */
public class OnyxGame extends Game {

    private static final Log log = Log.create("Onyx");

    public static ProjectConfig projectConfig;

    private static TextComponent debugComponent;
    private static boolean debug = false;

    @Override
    public void create() {

        // Load config

        projectConfig = Configs.loadCached(ProjectConfig.class);

        // Force pre-caching of default font

        FTFGeneratorCache.getFTFGenerator(projectConfig.default_font);

        // Print debug info

        log.print("Onyx %s starting", projectConfig.version);
        log.print("Current libGDX version is %s", Version.VERSION);
        log.print("Current backend is %s/%s", Gdx.app.getType(), System.getProperty("os.name"));
        log.print("Current JRE version is %s", System.getProperty("java.version"));

        // Setup cameras

        OrthographicCamera worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false);
        worldCam.viewportWidth = Gdx.graphics.getWidth();
        worldCam.viewportHeight = Gdx.graphics.getHeight();

        OrthographicCamera guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false);
        guiCam.viewportWidth = Gdx.graphics.getWidth();
        guiCam.viewportHeight = Gdx.graphics.getHeight();

        // Setup GameManager

        GameManager.setWorldCamera(worldCam);
        GameManager.setGuiCamera(guiCam);

        GameManager.setRenderer(new ShapeRenderer());
        GameManager.getRenderer().setAutoShapeType(true);

        GameManager.setBatch(new SpriteBatch());

        GameManager.setFont(new BitmapFont());

        // Init console

        Console.init();

        // Init PolygonLoader

        PolygonLoader.init();

        // Init KeymapLoader

        KeymapLoader.init();

        // Setup Initial Screen

        Console.dispatchCommand("screen " + projectConfig.initial_screen);

        // Setup info component

        debugComponent = new TextComponent("");

    }


    public void update() {

        // Open console on key press

        if (Gdx.input.isKeyJustPressed(KeymapLoader.getKeyCode("game_toggle_debug"))) {
            toggleDebug();
        }

        // Update console

        Console.update();

        // Update camera

        GameManager.getWorldCamera().update();

        if (ScreenManager.getCurrentScreen() != getScreen())
            setScreen(ScreenManager.getCurrentScreen());

        // Update debug info component

        debugComponent.setText(DebugInfo.get());

    }

    @Override
    public void render() {
        update();

        // Clear screen

        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render frame

        GameManager.setIsRendering(true);
        getScreen().render(Utils.delta());
        GameManager.setIsRendering(false);

        // Draw console

        DebugInfo.frameTimes.add(Utils.delta());

        GameManager.setIsRendering(true);
        if (debug)
            Console.draw();
        else debugComponent.draw(GameManager.getBatch(), 20, Gdx.graphics.getHeight() - 10);
        GameManager.setIsRendering(false);

    }

    @Override
    public void dispose() {

        // Dispose active screen

        ScreenManager.getCurrentScreen().dispose();

        // Dispose various resources

        GameManager.dispose();
        FTFGeneratorCache.dispose();
        PolygonLoader.dispose();

    }

    private static void toggleDebug() {
        debug = ! debug;
    }

    @Override
    public void resize(int width, int height) {

        // Update cameras

        OrthographicCamera worldCam = GameManager.getWorldCamera();
        worldCam.setToOrtho(false);
        worldCam.viewportWidth = Gdx.graphics.getWidth();
        worldCam.viewportHeight = Gdx.graphics.getHeight();

        // ? fixme
        GameManager.getBatch().setProjectionMatrix(worldCam.combined);

        OrthographicCamera guiCam = GameManager.getGuiCamera();
        guiCam.setToOrtho(false);
        guiCam.viewportWidth = Gdx.graphics.getWidth();
        guiCam.viewportHeight = Gdx.graphics.getHeight();

    }

}

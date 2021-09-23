package test;

import controller.DrawingLoop;
import controller.GameLoop;
import model.Character;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.mvvmfx.testingutils.jfxrunner.JfxRunner;
import view.Platform;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JfxRunner.class)
public class CharacterTest {
    private static final int CHARACTER_HEIGHT = 64;
    private Character floatingCharacter;
    private Character floatingCharacter2;

    private ArrayList<Character> characterListUnderTest;
    private Platform platformUnderTest;
    private GameLoop gameLoopUnderTest;
    private DrawingLoop drawingLoopUnderTest;
    private Method updateMethod;
    private Method redrawMethod;

    @Before
    public void setup() {
        floatingCharacter = new Character(30, 30, 0, 0, KeyCode.A, KeyCode.D, KeyCode.W);
        floatingCharacter2 = new Character(Platform.WIDTH - 60, 30, 0, 96, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);
        characterListUnderTest = new ArrayList<Character>(2);

        characterListUnderTest.add(floatingCharacter);
        characterListUnderTest.add(floatingCharacter2);

        platformUnderTest = new Platform();
        gameLoopUnderTest = new GameLoop(platformUnderTest);
        drawingLoopUnderTest = new DrawingLoop(platformUnderTest);
        try {
            updateMethod = GameLoop.class.getDeclaredMethod("update", ArrayList.class);
            redrawMethod = DrawingLoop.class.getDeclaredMethod("paint", ArrayList.class);
            updateMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            updateMethod = null;
            redrawMethod = null;
        }
    }
    @Test
    public void characterInitialValuesShouldMatchConstructorArguments() {
        assertEquals("Initial x", 30, floatingCharacter.getX(), 0);
        assertEquals("Initial y", 30, floatingCharacter.getY(), 0);
        assertEquals("Offset x", 0, floatingCharacter.getOffsetX(), 0.0);
        assertEquals("Offset y", 0, floatingCharacter.getOffsetY(), 0.0);
        assertEquals("Left key", KeyCode.A, floatingCharacter.getLeftKey());
        assertEquals("Right key", KeyCode.D, floatingCharacter.getRightKey());
        assertEquals("Up key", KeyCode.W, floatingCharacter.getUpKey());
    }

    @Test
    public void characterShouldMoveToTheLeftAfterTheLeftKeyIsPressed() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.A);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertTrue("Controller: Left key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model: Character moving left state is set", isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving left", characterUnderTest.getX() < startX);
    }

    //1
    @Test
    public void characterShouldMoveToTheRightAfterTheRightKeyIsPressed() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveRight = characterUnderTest.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);
        assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving right", characterUnderTest.getX() > startX);
    }

    //2
    @Test
    public void characterShouldJumpAfterTheUpKeyIsPressed() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
//        int startOffsetY = characterUnderTest.getOffsetY();
        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterUnderTest.setOnGround();
        assertTrue("Controller: Up key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.W));
//        assertTrue("View: Character is Jump", characterUnderTest.getOffsetY() > startOffsetY);
        assertTrue("Model: Character is on the ground state is set", characterUnderTest.getY() == Platform.GROUND);
        assertTrue("View: Character is can Jump", characterUnderTest.JumpCheck() == true);
    }

    //3
    @Test
    public void characterShouldNotJumpAfterTheUpKeyIsPressedInTheAir() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
//        int startOffsetY = characterUnderTest.getOffsetY();
        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterUnderTest.jump();
        assertTrue("Controller: Up key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.W));
//        assertTrue("View: Character is Jump", characterUnderTest.getOffsetY() > startOffsetY);
        assertTrue("Model: Character is not on the ground state is set", characterListUnderTest.get(0).getY() != Platform.GROUND);
        assertTrue("View: Character is can not Jump", characterListUnderTest.get(0).JumpCheck() == false);
    }

    //4
    @Test
    public void characterhitborder() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveRight = characterUnderTest.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);
        int round = 0;
        while (round<100){
            assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.D));
            assertTrue("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTest));
            characterUnderTest.moveX();
            characterUnderTest.checkReachGameWall();
            round++;
        }
//        characterUnderTest.checkReachGameWall();
        assertTrue("View: Character is moving right", characterUnderTest.getX() <= Platform.WIDTH && startX < characterUnderTest.getX());
    }

//    //5
//    public void collidedwiththeothercharacter() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException, InterruptedException {
//        Character characterUnderTest1 = characterListUnderTest.get(0);
//        Character characterUnderTest2 = characterListUnderTest.get(0);
//        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
//        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
//        Field isMoveRight1 = characterUnderTest1.getClass().getDeclaredField("isMoveRight");
//        Field isMoveRight2 = characterUnderTest2.getClass().getDeclaredField("isMoveRight");
//        int round = 0;
//        while (round<100){
//            assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.D));
//            assertTrue("Model: Character moving right state is set", isMoveRight1.getBoolean(characterUnderTest1));
//            assertTrue("Model: Character moving right state is set", isMoveRight2.getBoolean(characterUnderTest2));
//            characterUnderTest1.moveX();
//            characterUnderTest2.moveX();
//            characterUnderTest1.collided(characterUnderTest1);
//            characterUnderTest2.collided(characterUnderTest2);
//            round++;
//        }
//        assertTrue("check charracter collided",characterUnderTest1.getX() != characterUnderTest2.getX());
//    }

    //5 character ชนกับ other character
    @Test
    public void characterCollisionOnEachOther() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        characterListUnderTest.get(0).setX(Platform.WIDTH / 2);
        characterListUnderTest.get(1).setX(Platform.WIDTH/2);
        platformUnderTest.getKeys().add(KeyCode.D);
        platformUnderTest.getKeys().add(KeyCode.A);
        characterListUnderTest.get(0).collidedTest(characterListUnderTest.get(1));
        platformUnderTest.getKeys().add(KeyCode.LEFT);
        platformUnderTest.getKeys().add(KeyCode.RIGHT);
        assertTrue("Model: Character1 will not more than Character2 ",floatingCharacter.getX() <= floatingCharacter2.getX());
        assertTrue("Model: Character is collision",Character.checkStop());
        //assertEquals("Model: Character will not move", characterListUnderTest.get(0).getX() - (characterListUnderTest.get(1).getX()));
    }

    //6 เหยียบหัวอีกฝ่าย
    @Test
    public void characterStompedToAnotherCharacter() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest);
        characterListUnderTest.get(0).setY(Platform.HEIGHT/2+1);
        characterListUnderTest.get(1).setY(Platform.HEIGHT/2 + Character.CHARACTER_HEIGHT);
        characterListUnderTest.get(0).setX(Platform.HEIGHT/2);
        characterListUnderTest.get(1).setX(Platform.HEIGHT/2);
        assertTrue("Model: Character Stomped to each other", Character.stompedTest(characterListUnderTest.get(0)));
    }

    //7 โดนเหยียบ
    @Test
    public void characterGetStompedByAnotherCharacter() throws IllegalAccessException, InvocationTargetException, InterruptedException {
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterListUnderTest.get(1).setY(Platform.HEIGHT / 2 + 1);
        characterListUnderTest.get(0).setY(Platform.HEIGHT / 2 + Character.CHARACTER_HEIGHT);
        characterListUnderTest.get(1).setX(Platform.WIDTH / 2);
        characterListUnderTest.get(0).setX(Platform.WIDTH / 2);
        assertTrue("Model: Character Stomped To each other" ,characterListUnderTest.get(1)
                .stompedTest(characterListUnderTest.get(0)));
    }

//    //6
//    public void  characterstompstheother() throws IllegalAccessException, InvocationTargetException, InvocationTargetException, InvocationTargetException, NoSuchFieldException {
//        Character characterUnderTest = characterListUnderTest.get(0);
//        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
//        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
//        Field falling = characterUnderTest.getClass().getDeclaredField("falling");
//        falling.setAccessible(true);
//        assertTrue("check falling",falling.getBoolean(characterUnderTest));
//        assertFalse("check stomped",characterUnderTest.checkstomped(characterUnderTest));
//    }

}

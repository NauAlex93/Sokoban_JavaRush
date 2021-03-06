package com.javarush.task.task34.task3410.model;

import com.javarush.task.task34.task3410.controller.EventListener;

import java.nio.file.Paths;

public class Model {

   public static int FIELD_CELL_SIZE = 20;

   public GameObjects gameObjects;
   public int currentLevel = 1;
   private LevelLoader levelLoader = new LevelLoader(Paths.get(".\\4.JavaCollections\\src\\com\\javarush\\task\\task34\\task3410\\res\\levels.txt"));

   EventListener eventListener;

   public GameObjects getGameObjects() {

      return gameObjects;
   }

   public void restartLevel(int level) {

      this.gameObjects = levelLoader.getLevel(level);
   }

   public void restart() {

      restartLevel(currentLevel);
   }

   public void startNextLevel() {
      currentLevel = currentLevel + 1;
      restartLevel(currentLevel);
   }

   public void setEventListener(EventListener eventListener){

      this.eventListener = eventListener;
   }

   public void move(Direction direction){

      Player player = gameObjects.getPlayer();

      if (checkWallCollision(player, direction)) {
         return;
      }
      if (checkBoxCollisionAndMoveIfAvaliable(direction)){
         return;
      }

      int sellSize = FIELD_CELL_SIZE;
      switch (direction) {
         case LEFT:
            player.move(-sellSize, 0);
            break;
         case RIGHT:
            player.move(sellSize, 0);
            break;
         case UP:
            player.move(0, -sellSize);
            break;
         case DOWN:
            player.move(0, sellSize);
      }
      checkCompletion();
   }

   public boolean checkWallCollision(CollisionObject gameObject, Direction direction){

      for (Wall wall : gameObjects.getWalls()){

         if(gameObject.isCollision(wall, direction)){
            return true;
         }
      }
      return false;
   }

   public boolean checkBoxCollisionAndMoveIfAvaliable(Direction direction){

      Player player = gameObjects.getPlayer();

      GameObject  stopped = null;
      for (GameObject gameObject: gameObjects.getAll()){
         if (!(gameObject instanceof Player)&&!(gameObject instanceof Home) && player.isCollision(gameObject, direction)){
            stopped = gameObject;
         }
      }


      if ((stopped == null)){
         return false;
      }
      if (stopped instanceof Box){
         Box stoppedBox = (Box)stopped;
         if (checkWallCollision(stoppedBox, direction)){
            return true;
         }
         for (Box box : gameObjects.getBoxes()){
            if(stoppedBox.isCollision(box, direction)){
               return true;
            }
         }
         switch (direction)
         {
            case LEFT:
               stoppedBox.move(-FIELD_CELL_SIZE, 0);
               break;
            case RIGHT:
               stoppedBox.move(FIELD_CELL_SIZE, 0);
               break;
            case UP:
               stoppedBox.move(0, -FIELD_CELL_SIZE);
               break;
            case DOWN:
               stoppedBox.move(0, FIELD_CELL_SIZE);
         }
      }
      return false;

   }

   public void checkCompletion() {

      boolean yes = true;

      for(Home home : gameObjects.getHomes()){
         boolean current = false;

         for (Box box: gameObjects.getBoxes()){
            if ((box.getX() == home.getX()) && (box.getY() == home.getY()))
               current = true;
         }

         if (!current)yes = false;
      }

      if (yes)
         eventListener.levelCompleted(currentLevel);
   }
}

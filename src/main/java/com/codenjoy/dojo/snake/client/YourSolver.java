package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

import java.util.List;

/**
 * User: erom.mdn@gmail.com
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        System.out.println(board.toString());

        Point applePos = board.getApples().get(0);
        Point headPos = board.getHead();

        int headX;
        int headY;
        int targetX = applePos.getX();
        int targetY = applePos.getY();
        if(board.getHead() == null){
            return Direction.UP.toString();
        } else{
            headX = headPos.getX();
            headY = headPos.getY();
        }
        boolean[] dirs = avoidSuicide();
        /*  result[0] = up;
            result[1] = right;
            result[2] = down;
            result[3] = left; */

        //Target is on the bottom row, to the right from the header.
        if (targetY == 0 && headY != 0 && targetX > headX){
            return moveRight();
        // Target on the bottom row, to the left or right under the header.
        } else if (targetY == 0 && headY != 0 && targetX <= headX){
            return moveDown();
        // Target and header both on the bottom row, target is to the left from the header.
        } else if (targetY == 0 && headY ==0 && targetX < headX){
            return moveLeft();
        // Target and head both on the bottom row, target is to the right from the header.
        } else if (targetY == 0 && headY == 0 && targetX > headX){
            if (headX - 1 > 0){
                return moveLeft();
            } else{
                return moveUp();
            }
        // target to the right and above the head
        } else if (targetX >= headX && targetY >= headY){
            if (targetY != headY){
                return moveUp();
            } else {
                return moveRight();
            }
        // Target to the right and below the head
        } else if (targetX >= headX && targetY < headY){
            if(targetX != headX){
                return moveRight();
            } else {
                return moveDown();
            }
        }





       /* if (targetX < headX && dirs[3]){

            return Direction.LEFT.toString();
        }

        if (targetX > headX && dirs[1]){

            return Direction.RIGHT.toString();
        }

        if (targetY < headY && dirs[2]){

            return Direction.DOWN.toString();

        }

        if (targetY > headY && dirs[0]){

            return Direction.UP.toString();
        }*/




        /*if(dirs[0]){
            return Direction.UP.toString();
        } else if (dirs[1]){
            return Direction.RIGHT.toString();
        } else if (dirs[2]){
            return Direction.DOWN.toString();
        } else if (dirs[3]){
            return Direction.LEFT.toString();
        }*/

        return Direction.UP.toString();
    }

    public boolean[] avoidSuicide(){
        Point head = board.getHead();
        Point stone = board.getStones().get(0);
        List<Point> walls = board.getBarriers();
        List<Point> snake = board.getSnake();
        Point potentialStep;

        boolean up = true;
        boolean down = true;
        boolean right = true;
        boolean left = true;

        boolean[] result = new boolean[4];
        // ---- top -----
        potentialStep = head.copy();
        potentialStep.move(head.getX(), head.getY()+1);
        if(potentialStep.itsMe(stone)){
            up = false;
        }
        for(Point el: walls){
            if(potentialStep.itsMe(el)){
                up = false;
            }
        }
        for(Point el: snake){
            if(potentialStep.itsMe(el)){
                up = false;
            }
        }
        // ---- Bot -----
        potentialStep = head.copy();
        potentialStep.move(head.getX(), head.getY()-1);
        if(potentialStep.itsMe(stone)){
            down = false;
        }
        for(Point el: walls){
            if(potentialStep.itsMe(el)){
                down = false;
            }
        }
        for(Point el: snake){
            if(potentialStep.itsMe(el)){
                down = false;
            }
        }
        // ---- Right -----
        potentialStep = head.copy();
        potentialStep.move(head.getX()+1, head.getY());
        if(potentialStep.itsMe(stone)){
            right = false;
        }
        for(Point el: walls){
            if(potentialStep.itsMe(el)){
                right = false;
            }
        }
        for(Point el: snake){
            if(potentialStep.itsMe(el)){
                right = false;
            }
        }
        // ---- Left -----
        potentialStep = head.copy();
        potentialStep.move(head.getX()-1, head.getY());
        if(potentialStep.itsMe(stone)){
            left = false;
        }
        for(Point el: walls){
            if(potentialStep.itsMe(el)){
                left = false;
            }
        }
        for(Point el: snake){
            if(potentialStep.itsMe(el)){
                left = false;
            }
        }
        result[0] = up;
        result[1] = right;
        result[2] = down;
        result[3] = left;

        return result;



    }

    public String moveLeft(){
        return Direction.LEFT.toString();
    }

    public String moveRight(){
        return Direction.RIGHT.toString();
    }

    public String moveUp(){
        return Direction.UP.toString();
    }

    public String moveDown(){
        return Direction.DOWN.toString();
    }



    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://104.248.24.36/codenjoy-contest/board/player/erom.mdn@gmail.com?code=12345678901234567890",
                new YourSolver(new RandomDice()),
                new Board());
    }

}

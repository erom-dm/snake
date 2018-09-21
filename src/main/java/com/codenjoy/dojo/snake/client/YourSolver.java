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
import com.codenjoy.dojo.services.*;

import java.util.*;

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

        int snakeLen = board.getSnake().size();
        Point applePos = board.getApples().get(0);
        Point headPos = board.getHead();
        Point stone = board.getStones().get(0);
        List<Point> walls = board.getWalls();
        List<Point> snake = board.getSnake();

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

        // Create Graph of nodes, connect them, mark obstacles.
        Graph g = new Graph(225);
        createGraph(g);
        g.addBarriers(nodeNumberFromPoint(stone));
        for(Point wall:walls){
            g.addBarriers(nodeNumberFromPoint(wall));
        }
        for(Point el:snake){
            g.addBarriers(nodeNumberFromPoint(el));
        }


        int headNode = nodeNumberFromCoord(headX, headY);
        int dest = nodeNumberFromPoint(applePos);

        List<Integer> res = g.BFS(headNode, dest);

        // get list of points with shortest path to the target
        LinkedList<Point> path = new LinkedList<>();
        if(!res.isEmpty()){
            for (Integer el: res){
                Point p = pointFromNodeNum(el);
                path.add(p);
            }
        }

        if(path.size() > 0){
            Point nextPoint = path.poll();
            String nextStep = moveToPoint(headPos, nextPoint);
            // Check if its possible to get out of the pocket, after picking up the apple.

            /*g = new Graph(225);
            createGraph(g);
            g.addBarriers(nodeNumberFromPoint(stone));
            for(Point wall:walls){
                g.addBarriers(nodeNumberFromPoint(wall));
            }
            for(Point el:snake){
                g.addBarriers(nodeNumberFromPoint(el));
            }*/

            /*if (checkPocket(g, nextPoint, applePos)){
                return nextStep;
            } else {
                return stepReverse(nextStep);
            }*/
            return nextStep;
        } else {
            boolean ignoreStone = false;
            boolean[] dirs = avoidSuicide(ignoreStone);
            //        result[0] = up;
            //        result[1] = right;
            //        result[2] = down;
            //        result[3] = left;

            if (dirs[1]){
                return Direction.RIGHT.toString();
            } else if (dirs[0]){
                return Direction.UP.toString();
            } else if (dirs[2]){
                return Direction.DOWN.toString();
            } else if (dirs[3]){
                return Direction.LEFT.toString();
            } else{
                ignoreStone = true;
                dirs = avoidSuicide(ignoreStone);
                if (dirs[1]){
                    return Direction.RIGHT.toString();
                } else if (dirs[0]){
                    return Direction.UP.toString();
                } else if (dirs[2]){
                    return Direction.DOWN.toString();
                } else if (dirs[3]){
                    return Direction.LEFT.toString();
                } else {
                    return Direction.UP.toString();
                }
            }
        }

    }

    private Point pointFromNodeNum(int nodeNum){
        int cord1 = nodeNum%15;
        int cord2 = (nodeNum - cord1)/15;
        return new PointImpl(cord1, cord2);
    }

    private int nodeNumberFromCoord(int x, int y){
        return x + 15 * y;
    }

    private int nodeNumberFromPoint(Point p){
        return p.getX() + 15 * p.getY();
    }

    private void createGraph(Graph g){
        // 15x15 field
        int node = -1;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                node += 1;
                if (j == 0 && i == 0) {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node + 15);
                } else if (j == 14 && i == 0) {
                    g.addEdge(node, node - 1);
                    g.addEdge(node, node + 15);
                } else if (j == 0 && i == 14) {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node - 15);
                } else if (j == 14 && i == 14) {
                    g.addEdge(node, node - 1);
                    g.addEdge(node, node - 15);
                } else if (i == 0) {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node + 15);
                    g.addEdge(node, node - 1);
                } else if (i == 14) {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node - 15);
                    g.addEdge(node, node - 1);
                } else if (j == 14) {
                    g.addEdge(node, node - 1);
                    g.addEdge(node, node - 15);
                    g.addEdge(node, node + 15);
                } else if (j == 0) {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node - 15);
                    g.addEdge(node, node + 15);
                } else {
                    g.addEdge(node, node + 1);
                    g.addEdge(node, node + 15);
                    g.addEdge(node, node - 15);
                    g.addEdge(node, node - 1);
                }
            }
        }
    }

    private boolean[] avoidSuicide(boolean ignoreStone){
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
        if(potentialStep.itsMe(stone) && !ignoreStone){
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
        if(potentialStep.itsMe(stone) && !ignoreStone){
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
        if(potentialStep.itsMe(stone) && !ignoreStone){
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
        if(potentialStep.itsMe(stone) && !ignoreStone){
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

    private String moveToPoint(Point source, Point target){
        if(target.getY() < source.getY()){
            return Direction.DOWN.toString();
        } else if (target.getY() > source.getY()){
            return Direction.UP.toString();
        } else if (target.getX() < source.getX()){
            return Direction.LEFT.toString();
        } else if (target.getX() > source.getX()){
            return Direction.RIGHT.toString();
        } else {
            ////ph
            return Direction.DOWN.toString();
        }
    }

    private boolean checkPocket(Graph g,Point left, Point right){
        List<Integer> res = g.BFS(nodeNumberFromPoint(left), nodeNumberFromPoint(right));
        LinkedList<Point> path = new LinkedList<>();
        if (!res.isEmpty()){
            for (Integer el: res){
                Point p = pointFromNodeNum(el);
                path.add(p);
            }
        }
        // returns false if 2 points are not connected, true if there is a path.
        return path.size() > 0;
    }

    private String stepReverse(String step){
        if (step.equals("RIGHT")){
            return "LEFT";
        } else if (step.equals("LEFT")){
            return "RIGHT";
        } else if (step.equals("UP")){
            return "DOWN";
        } else if (step.equals("DOWN")){
            return "UP";
        } else {
            return "RIGHT";
        }
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://104.248.24.36/codenjoy-contest/board/player/erom@gmail.com?code=12345678901234567890",
                new YourSolver(new RandomDice()),
                new Board());
    }

}

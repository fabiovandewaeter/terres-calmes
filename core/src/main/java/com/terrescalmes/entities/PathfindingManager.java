package com.terrescalmes.entities;

import java.util.*;
import com.badlogic.gdx.math.Vector2;
import com.terrescalmes.CollisionManager;
import com.terrescalmes.util.Vector2I;

public class PathfindingManager {

    private static PathfindingManager instance;

    private PathfindingManager() {
    }

    public static PathfindingManager getInstance() {
        if (instance == null) {
            instance = new PathfindingManager();
        }
        return instance;
    }

    /**
     * Trouve un chemin de la position de départ à la position cible
     * Utilise l'algorithme A*
     * 
     * @return Liste des positions à suivre (incluant la position de départ), ou
     *         null si pas de chemin
     */
    public List<Vector2I> findPath(Entity entity, Vector2I start, Vector2I goal, int maxIterations) {
        // Arrondir les positions aux cases
        int startX = Math.round(start.x);
        int startY = Math.round(start.y);
        int goalX = Math.round(goal.x);
        int goalY = Math.round(goal.y);

        // Si on est déjà à destination
        if (startX == goalX && startY == goalY) {
            List<Vector2I> path = new ArrayList<>();
            path.add(new Vector2I(startX, startY));
            return path;
        }

        // Vérifier si la destination est accessible
        if (!CollisionManager.getInstance().allowMove(entity, new Vector2I(goalX, goalY))) {
            System.out.println("Destination bloquée: (" + goalX + ", " + goalY + ")");
            return null;
        }

        // Structures de données pour A*
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<String> closedSet = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();

        // Nœud de départ
        Node startNode = new Node(startX, startY);
        startNode.gScore = 0;
        startNode.fScore = heuristic(startX, startY, goalX, goalY);

        openSet.add(startNode);
        allNodes.put(startNode.key(), startNode);

        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;

            Node current = openSet.poll();

            // Arrivé à destination
            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            closedSet.add(current.key());

            // Explorer les voisins (4 directions)
            int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

            for (int[] dir : directions) {
                int neighborX = current.x + dir[0];
                int neighborY = current.y + dir[1];
                String neighborKey = neighborX + "," + neighborY;

                // Ignorer si déjà exploré
                if (closedSet.contains(neighborKey)) {
                    continue;
                }

                // Vérifier si la case est accessible
                Vector2I neighborPos = new Vector2I(neighborX, neighborY);
                if (!CollisionManager.getInstance().allowMove(entity, neighborPos)) {
                    continue;
                }

                // Calculer le coût
                double tentativeGScore = current.gScore + 1.0;

                Node neighbor = allNodes.get(neighborKey);
                if (neighbor == null) {
                    neighbor = new Node(neighborX, neighborY);
                    allNodes.put(neighborKey, neighbor);
                }

                // Si on a trouvé un meilleur chemin vers ce voisin
                if (tentativeGScore < neighbor.gScore) {
                    neighbor.parent = current;
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + heuristic(neighborX, neighborY, goalX, goalY);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Pas de chemin trouvé
        System.out.println("Aucun chemin trouvé vers (" + goalX + ", " + goalY + ")");
        return null;
    }

    // /**
    // * Heuristique pour A* (distance de Manhattan)
    // */
    // private double heuristic(int x1, int y1, int x2, int y2) {
    // return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    // }

    /**
     * Heuristique pour A* (distance de Chebyshev)
     */
    private double heuristic(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    /**
     * Reconstruit le chemin depuis le nœud final
     */
    private List<Vector2I> reconstructPath(Node endNode) {
        List<Vector2I> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(0, new Vector2I(current.x, current.y));
            current = current.parent;
        }

        return path;
    }

    /**
     * Classe interne pour représenter un nœud dans l'algorithme A*
     */
    private static class Node {
        int x, y;
        double gScore = Double.POSITIVE_INFINITY; // Coût depuis le départ
        double fScore = Double.POSITIVE_INFINITY; // gScore + heuristique
        Node parent = null;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        String key() {
            return x + "," + y;
        }
    }
}

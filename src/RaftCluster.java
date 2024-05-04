public class RaftCluster {

    private static final int TOTAL_NODES = 4;
    private static RaftNode[] nodes = new RaftNode[TOTAL_NODES];
    public static RaftNode[] getNodes() {
        return nodes;
    }
    public static RaftNode createNode(int nodeId) {
        RaftNode node = new RaftNode(nodeId);
        nodes[nodeId - 1] = node;
        return node;
    }

    public static int getTotalNodes() {
        return TOTAL_NODES;
    }

    public static RaftNode getNode(int i) {
        if (i >= 1 && i <= TOTAL_NODES) {
            return nodes[i - 1];
        } else {
            throw new IllegalArgumentException("Invalid node index: " + i);
        }
    }
    public static void broadcastLeaderId(int leaderId) {
        System.out.println("Leader Id before broadcast " + leaderId);
        for (RaftNode node : nodes) {
            node.setLeader(leaderId);
            System.out.println("----------");
        }
    }
    public static void updateLeaderStatus(int newLeaderId) {
        for (RaftNode node : nodes) {
            node.setLeader(newLeaderId);
        }
    }
}

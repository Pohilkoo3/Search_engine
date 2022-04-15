package indexWeb;
import indexWeb.models.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class GetAllRefers extends RecursiveAction {
    private Node node;


    public GetAllRefers(String path) {
        this.node = new Node(path);
    }

    @Override
    protected void compute() {
        List<GetAllRefers> subTasks = new LinkedList<>();
        for(String child : node.getChild()) {
            GetAllRefers task = new GetAllRefers(child);
            subTasks.add(task);
            task.fork();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }
        for (GetAllRefers task : subTasks) {
          task.join();
        }
    }
}

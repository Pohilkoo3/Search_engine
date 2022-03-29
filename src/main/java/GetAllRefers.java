import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class GetAllRefers extends RecursiveAction {
    private Node node;
    private Session session;
    private Transaction transaction;

    public GetAllRefers(String path) {
        session = SessionFactory.getSession().openSession();
        transaction = session.beginTransaction();
        this.node = new Node(path, session, transaction);
    }

    @Override
    protected void compute() {
        List<GetAllRefers> subTasks = new LinkedList<>();
        for(String child : node.getChild()) {
            GetAllRefers task = new GetAllRefers(child);
            subTasks.add(task);
            task.fork(); // запустим асинхронно
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        for (GetAllRefers task : subTasks) {
          task.join();
        }
    }
}

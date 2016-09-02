package example.expense.user.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 2016-08-25.
 */
public class ExpenseListAdapter extends BaseAdapter{

    public Context context;
    private List<ExpenseListModel> lists;


    public ExpenseListAdapter(Context ctx){
        context = ctx;
    }

    public void setList(List list){
        lists = list;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(context, R.layout.expense_manager_list_item, null);
        }

        //TextView tv_expense_manager_userName = (TextView) convertView.findViewById(R.id.tv_expense_manager_userName);
        TextView tv_expense_manager_state = (TextView) convertView.findViewById(R.id.tv_expense_manager_state);
        TextView tv_expense_manager_use = (TextView) convertView.findViewById(R.id.tv_expense_manager_use);
        TextView tv_expense_manager_storeName = (TextView) convertView.findViewById(R.id.tv_expense_manager_storeName);
        TextView tv_expense_manager_amount = (TextView) convertView.findViewById(R.id.tv_expense_manager_amount);
        TextView tv_expense_manager_requestDay = (TextView) convertView.findViewById(R.id.tv_expense_manager_requestDay);

        //tv_expense_manager_userName.setText(lists.get(position).userName);
        tv_expense_manager_state.setText(lists.get(position).state);
        tv_expense_manager_use.setText(lists.get(position).summary);
        tv_expense_manager_storeName.setText(lists.get(position).storeName);
        tv_expense_manager_amount.setText(lists.get(position).amount);

        String dateString = lists.get(position).paymentDate;
        tv_expense_manager_requestDay.setText( String.format("%4s년 %2s월 %2s일", dateString.substring(0, 4), dateString.substring(4, 6), dateString.substring(6, 8)));


        return convertView;
    }
}

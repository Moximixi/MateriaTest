package com.yzq.zxinglibrary.android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yzq.zxinglibrary.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.ListListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private  ListListener mListener;

    private View view;//定义view用来设置fragment的layout
    public RecyclerView recyclerView;//定义RecyclerView
    //定义以Book实体类为对象的数据集合
    private ArrayList<Book> bookList = new ArrayList<Book>();
    //自定义recyclerveiw的适配器
    private CollectRecycleAdapter mCollectRecyclerAdapter;
    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_list, container, false);
        //对recycleview进行配置
        initRecyclerView();
        for (int i=0;i<20;i++) {
            Book book = new Book();
            book.setTitle("sadsa"+i);
            book.setAuthor("233");
            bookList.add(book);
        }
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.List_set();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListListener) {
            mListener = (ListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ListListener {
        // TODO: Update argument type and name
        void List_set();

    }


    /**
     * TODO 对recycleview进行配置
     */

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);
        //创建adapter
        mCollectRecyclerAdapter = new CollectRecycleAdapter(getActivity(), bookList);
        //给RecyclerView设置adapter
        recyclerView.setAdapter(mCollectRecyclerAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        mCollectRecyclerAdapter.setOnItemClickListener(new CollectRecycleAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Book data) {
                //此处进行监听事件的业务处理
                if(view.getId()==R.id.delete){
                    Toast.makeText(getActivity(),"删除",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(getActivity(), "我是item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //RecyclerView适配器
    public static class CollectRecycleAdapter extends RecyclerView.Adapter<CollectRecycleAdapter.myViewHodler> {
        private Context context;
        private ArrayList<Book> booksEntityList;

        //创建构造函数
        public CollectRecycleAdapter(Context context, ArrayList<Book> booksEntityList) {
            //将传递过来的数据，赋值给本地变量
            this.context = context;//上下文
            this.booksEntityList = booksEntityList;//实体类数据ArrayList
        }

        /**
         * 创建viewhodler，相当于listview中getview中的创建view和viewhodler
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public myViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            //创建自定义布局
            View itemView = View.inflate(context, R.layout.item_layout, null);
            return new myViewHodler(itemView);
        }

        /**
         * 绑定数据，数据与view绑定
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(myViewHodler holder, int position) {
            //根据点击位置绑定数据
            Book data = booksEntityList.get(position);
            holder.mItemImg.setImageBitmap(data.getBitmap());
            holder.mItemTitle.setText(data.getTitle());//获取实体类中的name字段并设置
            holder.mItemAuthor.setText(data.getAuthor());//获取实体类中的price字段并设置

        }

        /**
         * 得到总条数
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return booksEntityList.size();
        }

        //自定义viewhodler
        class myViewHodler extends RecyclerView.ViewHolder {
            private ImageView mItemImg;
            private TextView mItemTitle;
            private TextView mItemAuthor;
            private Button mItemDeldte;

            public myViewHodler(View itemView) {
                super(itemView);
                mItemImg = (ImageView) itemView.findViewById(R.id.item_img);
                mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
                mItemAuthor = (TextView) itemView.findViewById(R.id.item_author);
                mItemDeldte=(Button)itemView.findViewById(R.id.delete) ;
                mItemDeldte.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeData(getLayoutPosition());
                    }
                });
                //点击事件放在adapter中使用，也可以写个接口在activity中调用
                //方法一：在adapter中设置点击事件
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //可以选择直接在本位置直接写业务处理
                        //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                        //此处回传点击监听事件
                        if(onItemClickListener!=null){
                            onItemClickListener.OnItemClick(v, booksEntityList.get(getLayoutPosition()));
                        }
                    }
                });

            }
        }




        //  添加数据
        public void addData(int position,Book data) {
//      在list中添加数据，并通知条目加入一条
            this.booksEntityList.add(position, data);
            //添加动画
            notifyItemInserted(position);
        }

        //  删除数据
        public void removeData(int position) {
            this.booksEntityList.remove(position);
            //删除动画
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

        //**
         //* 设置item的监听事件的接口
         //*
        public interface OnItemClickListener {
            //**
            //* 接口中的点击每一项的实现方法，参数自己定义
            //*
            //* @param view 点击的item的视图
            //* @param data 点击的item的数据
            //*
            public void OnItemClick(View view, Book data);
        }

        //需要外部访问，所以需要设置set方法，方便调用
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }



    }

}

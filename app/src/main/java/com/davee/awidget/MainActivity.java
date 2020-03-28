package com.davee.awidget;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import app.davee.assistant.uitableview.NSIndexPath;
import app.davee.assistant.uitableview.TableViewDataSourceAdapter;
import app.davee.assistant.uitableview.UITableView;
import app.davee.assistant.uitableview.UITableViewCell;
import app.davee.assistant.uitableview.UITableViewDataSource;
import app.davee.assistant.uitableview.UITableViewDelegate;
import app.davee.assistant.uitableview.models.UITableViewCellModel;
import app.davee.assistant.uitableview.models.UITableViewModel;
import app.davee.assistant.uitableview.models.UITableViewSectionModel;

import android.content.Intent;
import android.os.Bundle;

import com.davee.awidget.examples.ActionSheetExampleActivity;
import com.davee.awidget.examples.CirclePickerExampleActivity;
import com.davee.awidget.examples.CircleSliderExampleActivity;
import com.davee.awidget.examples.HorizonScaleExampleActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.davee.awidget.MainActivity.CellName.*;

public class MainActivity extends AppCompatActivity {
    
    @IntDef({scaleBar, circleSlider, circlePicker})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CellName {
        int scaleBar = 0x10;
        int circleSlider = 0x11;
        int circlePicker = 0x12;
        int actionSheet = 0x13;
    }
    
    private UITableView mTableView;
    private UITableViewModel mTableViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTableView = findViewById(R.id.tableView);
        mTableView.setTableViewDataSource(this.tableViewDataSource());
        mTableView.setTableViewDelegate(this.tableViewDelegate());
    }
    
    private void setupTableViewModel() {
        mTableViewModel = new UITableViewModel();
        {
            // Section 0
            UITableViewSectionModel sectionModel = mTableViewModel.appendNewSectionModel();
    
            sectionModel.addCellModel(makeCellModel(scaleBar, "刻度尺"));
            sectionModel.addCellModel(makeCellModel(circleSlider, "圆形调节器"));
            sectionModel.addCellModel(makeCellModel(circlePicker, "圆形选择器"));
        }
        
        {
            // Section 1
            UITableViewSectionModel sectionModel = mTableViewModel.appendNewSectionModel();
    
            sectionModel.addCellModel(makeCellModel(actionSheet, "Action Sheet"));
        }
        
    }
    
    private UITableViewCellModel makeCellModel(int name, String title) {
        UITableViewCellModel cell1 = new UITableViewCellModel();
        cell1.setCellId(name);
        cell1.titleText = title;
        cell1.accessoryType = UITableViewCell.AccessoryType.Disclosure;
        return cell1;
    }
    
    private UITableViewDataSource tableViewDataSource() {
        this.setupTableViewModel();
        return new TableViewDataSourceAdapter(mTableViewModel);
    }
    
    private UITableViewDelegate tableViewDelegate() {
        return new UITableViewDelegate(){
            @Override
            public void onTableViewCellClicked(UITableView tableView, UITableViewCell tableViewCell, NSIndexPath indexPath) {
                // super.onTableViewCellClicked(tableView, tableViewCell, indexPath);
                final UITableViewCellModel cellModel = mTableViewModel.cellModelAtIndexPath(indexPath);
                if (cellModel.getCellId() == scaleBar) {
                    startActivity(HorizonScaleExampleActivity.class);
                } else if (cellModel.getCellId() == circleSlider) {
                    startActivity(CircleSliderExampleActivity.class);
                } else if (cellModel.getCellId() == circlePicker) {
                    startActivity(CirclePickerExampleActivity.class);
                } else if (cellModel.getCellId() == actionSheet) {
                    startActivity(ActionSheetExampleActivity.class);
                }
            }
        };
    }
    
    private void startActivity(Class className) {
        startActivity(new Intent(MainActivity.this, className));
    }
}

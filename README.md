# AutoFitGridLayout
It is a layout like GridLayout. But children in it will fill the column.

# How to Use?
Just copy the class `AutoFitGridLayout.java` as your custom widget.

In the xml file:

    <com.liuzhuang.afgridlayout.AutoFitGridLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingBottom="@dimen/activity_vertical_margin"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        app:columnCount="2"
        app:horizontalSpace="4.5dp"
        app:verticalSpace="9dp">
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/darker_gray"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/holo_blue_bright"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/holo_blue_dark"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/darker_gray"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/holo_blue_bright"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@android:color/holo_blue_dark"/>
    </com.liuzhuang.afgridlayout.AutoFitGridLayout>
    
And the effect:

![effect](./effect.png)

# License

	Copyright 2015 Liu Zhuang

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

   		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

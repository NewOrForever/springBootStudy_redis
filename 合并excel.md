excel2013快速把多个工作表合并到一个excel表


日常工作当中，会产生很多Excel文档，每当需要整理的时候又很难归类、查询。这个时候就需要把诺干个Excel文档，合并到一个Excel文档里。一个Excel文档对应一个Sheet工作表，方便查询、归类、整理。下面小编就为大家介绍Excel2013把多个文件合并到一个Excel文档里方法，喜欢的朋友一起来看看吧！
表格合并

1、把需要合并的excel表格文档放到同一个文件夹（例如：我放在了数据合并这个文件夹）里



2、新建一个“数据合并.xlsx“文档（最好放在数据合并这个文件夹，虽然最后生成了一个新的excel）



3、打开“数据合并.xlsx“文档，在”Sheet1“工作表的地方右键→查看代码（快捷键：“Alt+F11”，尽量选用快捷方式）进入到Microsoft Visual Basic for Applications窗口





4、双击工程资源管理器里面的sheet1，在右侧的代码区粘贴如下代码：

Sub 合并当前目录下所有工作簿的全部工作表()

Dim MyPath, MyName, AWbName

Dim Wb As Workbook, WbN As String

Dim G As Long

Dim Num As Long

Dim BOX As String

Application.ScreenUpdating = False

MyPath = ActiveWorkbook.Path

MyName = Dir(MyPath & "\" & "*.xls")

AWbName = ActiveWorkbook.Name

Num = 0

Do While MyName <> ""

If MyName <> AWbName Then

Set Wb = Workbooks.Open(MyPath & "\" & MyName)

Num = Num + 1

With Workbooks(1).ActiveSheet

.Cells(.Range("A65536").End(xlUp).Row + 2, 1) = Left(MyName, Len(MyName) - 4)

For G = 1 To Sheets.Count

Wb.Sheets(G).UsedRange.Copy .Cells(.Range("A65536").End(xlUp).Row + 1, 1)

Next

WbN = WbN & Chr(13) & Wb.Name

Wb.Close False

End With

End If

MyName = Dir

Loop

Range("A1").Select

Application.ScreenUpdating = True

MsgBox "共合并了" & Num & "个工作薄下的全部工作表。如下：" & Chr(13) & WbN, vbInformation, "提示"

End Sub





5、运行→运行子过程/用户窗体（或者直接点击运行按钮，快捷键：F5），即可合并所有Excel表格到”数据合并.xlsx“文档的Sheet1工作表里面



6、完成Excel表格的合并



注意事项

本方法只在Microsoft Excel 2013软件下测试通过
在2016版也测试通过

2013之前的版本，数据量一大容易卡顿，建议使用最新版

其他的offce软件，尚未测试

以上就是Excel2013把多个文件合并到一个Excel文档里方法介绍，大家学会了吗？希望这篇文章能对大家有所帮助

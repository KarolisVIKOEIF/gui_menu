

package lt.labrencis.menu


import scala.collection.mutable.ArrayBuffer

import scala.swing._
import swing.event._
import swing.GridBagPanel._
import java.awt.{Color, Font}
import javax.swing.table.DefaultTableModel


object MenuApp extends SimpleSwingApplication {
  val appX = 1100
  val appY = 750
  val appDimension = new Dimension(appX, appY)

  def setSz(d: Dimension, t: Component) = {
    t.minimumSize = d
    t.preferredSize = d
    t.maximumSize = d
  }

  lazy val ui = new GridBagPanel {
    def getBottomButtonAddedToPanelBoxPanel(txt: String, p: BoxPanel): Button = {
      val b = new Button(txt) {
        val sz = new Dimension(appX / 3 - 3, 20)
        setSz(sz, this)
        font = new Font("Verdana", Font.PLAIN, 20)
      }
      listenTo(b)
      p.contents += b
      b
    }

    val topBox = new BoxPanel(Orientation.Horizontal) {
      val sz = new Dimension(appX, 30)
      setSz(sz, this)

    }

    val callWaiterButton = getBottomButtonAddedToPanelBoxPanel("Call waiter", topBox)
    val submitOrderButton = getBottomButtonAddedToPanelBoxPanel("Submit order", topBox)
    val helpButton = getBottomButtonAddedToPanelBoxPanel("Help", topBox)

    def getListViewBox(m: Seq[String], sz: Dimension, title: String) = {
      val lv = new ListView(m) {
        //border = Swing.LineBorder(Color.red)
        font = new Font("Verdana", Font.PLAIN, 16)
        fixedCellHeight = 70
      }
      listenTo(lv.selection)

      val sp = new ScrollPane {
        preferredSize = sz
        minimumSize = sz
        maximumSize = sz
        contents = lv
      }
      val bp = new BorderPanel {
        val l = new Label(title) {
          setSz(new Dimension(100, 30), this)
          //border = Swing.LineBorder(Color.yellow, 3)
          font = new Font("Verdana", Font.PLAIN, 16)
        }

        layout(l) = BorderPanel.Position.North
        layout(sp) = BorderPanel.Position.Center
      }
      (bp, lv)
    }

    val m = new Menu()

    val menuListDimension = new Dimension(200, 400)
    val (categoryBox, categoryListView) = getListViewBox(m.getCategoryList, menuListDimension, "Choose a category:")
    val (dishesBox, dishesListView) = getListViewBox(List(), new Dimension(300, menuListDimension.height), "Choose dish:")
    dishesBox.visible = false

    class MyFuckingModel(var data: ArrayBuffer[Array[String]], val columnNames: List[String]) extends DefaultTableModel {
      override def getColumnName(col: Int) = columnNames(col)

      override def isCellEditable(row: Int, col: Int) = false

      override def getRowCount = data.length

      override def getColumnCount = columnNames.length

      override def getValueAt(row: Int, col: Int) = {
        if (col != 3) data(row)(col).asInstanceOf[AnyRef]
        else {
          import scala.math.BigDecimal
          val currRow = data(row)
          val quantity = new java.math.BigDecimal(currRow(2))
          val price = new java.math.BigDecimal(currRow(1))
          (new BigDecimal(quantity) * new BigDecimal(price)).toString()
        }
      }

      def setValueAt(aValue: String, row: Int, col: Int) {
        data(row)(col) = aValue
        //updateTotal()
      }

      def removeAt(row: Int) {
        if (data.length > row) {
          data remove row
          this.fireTableDataChanged()
        }
      }

      def insertNew(row: Array[String]) {
        data.append(row)
      }
    }

    // Orderlist
    val dt = ArrayBuffer[Array[String]]()

    val colNames = List("Dish name", "Price", "Quantity", "ForThisItem")

    val orderTable = new Table() {
      model = new MyFuckingModel(dt, colNames)
      selection.intervalMode = Table.IntervalMode.Single
      selection.elementMode = Table.ElementMode.Row
      font = new Font("Verdana", Font.CENTER_BASELINE, 22)
      rowHeight = 30
      showGrid = true
      gridColor = Color.RED
      println(showGrid)
    }
    listenTo(orderTable.selection)


    val orderScrollPane = new ScrollPane {
      contents = orderTable
    }

    val orderTotalLabel = new Label("Total amount: $0")
    val orderPanel = new BoxPanel(Orientation.Vertical) {
      val sz = new Dimension(appX, 350)
      setSz(sz, this)

      //border = Swing.LineBorder(Color.green)
      val labelFont = new Font("Verdana", Font.CENTER_BASELINE, 17)
      contents += new Label("Your basket (click on row to decrease count of item by one. If the current count is one, item will be removed)") {
        font = labelFont
      }
      contents += orderScrollPane
      contents += orderTotalLabel

    }

    val mainNameLabel = new Label("") {
      setSz(new Dimension(300, 40), this)
      //border = Swing.LineBorder(Color.red, 3)

    }

    val mainDescTextArea = new TextArea() {
      //setSz(new Dimension(550, 200), this)
      //border = Swing.LineBorder(Color.yellow, 3)
      editable = false
    }
    val mainDescScroll = new ScrollPane() {
      setSz(new Dimension(600, 150), this)
      contents = mainDescTextArea
    }

    val mainPriceLabel = new Label("")

    val mainOrderButton = new Button("Add to basket") {
      var sz = new Dimension(150, preferredSize.height)
      setSz(sz, this)
    }
    listenTo(mainOrderButton)

    val mainTopPanel = new FlowPanel() {
      setSz(new Dimension(700, 60), this)
      contents += mainNameLabel
      contents += mainPriceLabel
      contents += mainOrderButton
      //border = Swing.LineBorder(Color.orange, 3)
    }


    val imgPanel = new ImagePanel {
      //border = Swing.LineBorder(Color.red, 3)
    }

    val mainPanel = new BoxPanel(Orientation.Vertical) {
      setSz(new Dimension(590, 600), this)
      //border = Swing.LineBorder(Color.cyan, 2)
      contents += mainTopPanel
      contents += imgPanel
      contents += mainDescScroll
      contents += new TextArea(theHelpText) {
        editable = false
        font = new Font("Verdana", Font.PLAIN, 20)
        visible = false
      }
    }
    mainPanel.contents.foreach(x => x.visible = ! x.visible)
    val mainItems = mainPanel.contents.toList.filter(! _.visible)
    val helpItems = mainPanel.contents.toList.filter(_.visible)


    def updateTotal() {
      import java.math.BigDecimal
      var sum = new BigDecimal("0")
      for (i <- 0 until orderTable.model.asInstanceOf[MyFuckingModel].data.length) {
        val current = new BigDecimal(orderTable.model.asInstanceOf[MyFuckingModel].getValueAt(i, 3).asInstanceOf[String])
        sum = sum.add(current)
      }
      orderTotalLabel.text = "Total amount: $" + sum.toString
    }

    def changeMainPanelMode(helpMode:Boolean=false) {
      def makeVisible(c: Component) = c.visible = true
      def makeInvisible(c: Component) = c. visible = false
      def hideMainViewShowHelp() {
        mainItems.foreach(makeInvisible)
        helpItems.foreach(makeVisible)
      }
      def hideHelpShowMain() {
        mainItems.foreach(makeVisible)
        helpItems.foreach(makeInvisible)
      }
      if (helpMode) hideMainViewShowHelp() else hideHelpShowMain()
    }

    var dishSelected = false
    reactions += {
      case TableRowsSelected(_, _, true) => // Orders table
        val cells = orderTable.selection.cells.toSeq
        val model = orderTable.model.asInstanceOf[MyFuckingModel]
        if (cells.length > 0) {
          val currentIdx = cells(0)._1
          val currentCount = model.data(currentIdx)(2).toInt
          if (currentCount == 1) orderTable.model.asInstanceOf[MyFuckingModel].removeAt(cells(0)._1)
          else {
            model.setValueAt((currentCount - 1).toString, currentIdx, 2)
            orderTable.selection.cells.clear()
          }
        }

        if (orderTable.model.asInstanceOf[MyFuckingModel].data.length > 0) {
          try {
            orderTable.selection.cells.clear()
            orderTable.selection.columns.clear()
          } catch {
            case iae: IllegalArgumentException => println("Px: " + iae + "\n")
          }
        }


      case ListSelectionChanged(x, _, false) => x match {
        case `categoryListView` =>
          dishSelected = false
          val sel = categoryListView.selection.leadIndex
          dishesListView.listData = m.getDishesInCategory(sel).toList.map(x => x.name.take(22) + "...") // exc here
          changeMainPanelMode(helpMode = true)
          dishesBox.visible = true

        case `dishesListView` =>
          def newlineAte(s: String) = {
            var spaceCount = 0
            s.toCharArray.map {
              x =>
                if (x == ' ') {
                  spaceCount += 1
                  if (spaceCount % 12 == 0) '\n'
                  else ' '
                } else
                  x
            }.mkString
          }
          dishSelected = true
          val catIdx = categoryListView.selection.leadIndex
          val dishIdx = dishesListView.selection.leadIndex
          m.getDish(catIdx, dishIdx) match {
            case Some(dish) =>
              changeMainPanelMode(helpMode = false)
              mainNameLabel.text = dish.name
              mainDescTextArea.text = newlineAte(dish.desc)
              //mainDescTextArea.text += "\n" + dish.imgName + "\n"
              mainPriceLabel.text = "Price: $" + dish.price
              imgPanel.imagePath = dish.imgPath
              imgPanel.repaint()

            case None => // NOP
          }
      }

      case ButtonClicked(x: Button) =>
        x match {
          case `callWaiterButton` => dishesBox.visible = true// nop(waiter)
          case `submitOrderButton` => // nop(submit)
          case `helpButton` => changeMainPanelMode(helpMode = true)
          case `mainOrderButton` =>
            if (dishSelected) {
              val catIdx = categoryListView.selection.leadIndex
              val dishIdx = dishesListView.selection.leadIndex

              m.getDish(catIdx, dishIdx) match {
                case Some(dish) =>
                  val mod = orderTable.model.asInstanceOf[MyFuckingModel]
                  val newRow = Array(dish.name, dish.price, "1", "")
                  mod.data find (x => x(0).toString == dish.name) match {
                    case Some(x) =>
                      val currIdx = mod.data.indexOf(x)
                      val currCount = mod.data(currIdx)(2).toInt
                      mod.setValueAt((currCount + 1).toString, currIdx, 2)

                      mod.fireTableCellUpdated(currIdx, 2)
                      mod.fireTableCellUpdated(currIdx, 3)
                    case None =>
                      mod.data.append(newRow)
                      mod.fireTableDataChanged()
                  }
                  updateTotal()
                case None => // NOP
              }
            }
        }
    }

    //border = Swing.LineBorder(new Color(100, 100, 100), 3)
    val c = new Constraints

    c.fill = Fill.Horizontal
    c.weightx = 0.0
    c.gridwidth = 3
    c.gridx = 0
    c.gridy = 0
    c.anchor = Anchor.North
    //layout(topBox) = c



    c.gridwidth = 1
    c.fill = Fill.Vertical
    c.gridy = 0
    c.gridx = 0
    c.weighty = 3
    c.anchor = Anchor.North
    layout(categoryBox) = c

    c.gridx = 1
    c.anchor = Anchor.North
    layout(dishesBox) = c

    c.gridx = 0
    c.gridy = 1
    c.gridwidth = 3
    layout(orderPanel) = c

    val cc = new Constraints
    cc.gridx = 2
    cc.gridy = 0
    cc.anchor = Anchor.East
    layout(mainPanel) = cc

    c.fill = Fill.Horizontal
    c.weightx = 0.0
    c.weighty = 0
    c.gridwidth = 3
    c.gridx = 0
    c.gridy = 2
    c.anchor = Anchor.Center
    layout(topBox) = c
  }

  def top = new MainFrame {
    //javax.swing.UIManager.setLookAndFeel(new NimbusLookAndFeel)
    title = "My Awesome Menu"
    contents = ui
    size = appDimension
  }

  val place = "EFONIO"
  val theHelpText =
    s"""Welcome to $place!
      |
      |Choose a category from a list on the left,
      |then choose dish from the second list and click
      |"Add to basket"
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |     Images from: https://www.panerabread.com/
    """.stripMargin
}

# Sơ đồ lớp UML — Mini Text Editor

## Tổng quan phân cấp

```mermaid
classDiagram
    direction TB

    class Editable {
        <<interface>>
        +getContent() String
        +setContent(content)
        +insertText(offset, text)
        +deleteText(offset, length)
        +canUndo() boolean
        +canRedo() boolean
        +undo()
        +redo()
    }

    class Document {
        <<abstract>>
        -title: String
        -file: File
        -modified: boolean
        +getTitle() String
        +getDisplayTitle() String
        +getContent()* String
        +setContent(content)*
        +computeStatistics()* TextStatistics
    }

    class TextDocument {
        -swingDocument: PlainDocument
        -undoManager: UndoManager
        +getSwingDocument()
        +getContent() String
        +setContent(content)
        +computeStatistics() TextStatistics
        +undo()
        +redo()
    }

    class TextStatistics {
        -characters: int
        -words: int
        -lines: int
        -paragraphs: int
        -letterFrequency: Map
        +fromText(text)$ TextStatistics
        +toSummaryTableData() Object[][]
    }

    class FindReplaceService {
        +findNext(...) SearchResult
        +findPrevious(...) SearchResult
        +replaceAll(...) int
    }

    class RecentFilesManager {
        -recentFiles: List~File~
        +add(file)
        +getDisplayNames() String[]
    }

    Document <|-- TextDocument : kế thừa
    Editable <|.. TextDocument : triển khai
    TextDocument --> TextStatistics : computeStatistics()
    FindReplaceService ..> Editable : thay thế qua
    MainFrame --> TextDocument
    MainFrame --> RecentFilesManager
    EditorTabPanel --> TextDocument
    StatisticsDialog --> TextStatistics
    FindReplaceDialog --> FindReplaceService
    ChartHelper ..> TextStatistics
```

## Tầng giao diện

```mermaid
classDiagram
    direction LR

    class MainFrame {
        -tabbedPane: JTabbedPane
        -statusBar: StatusBar
        +getActiveTab() EditorTabPanel
    }

    class EditorTabPanel {
        -document: TextDocument
        -textArea: JTextArea
    }

    class LineNumberPanel {
        +paintComponent(g) Graphics2D
    }

    class StatusBar
    class FindReplaceDialog
    class StatisticsDialog
    class AboutDialog
    class RecentFilesPanel {
        -list: JList
    }

    MainFrame *-- EditorTabPanel
    MainFrame *-- StatusBar
    MainFrame *-- RecentFilesPanel
    MainFrame --> FindReplaceDialog
    MainFrame --> StatisticsDialog
    MainFrame --> AboutDialog
    EditorTabPanel *-- LineNumberPanel
```

## Ánh xạ 4 nguyên lý OOP

| Nguyên lý | Hiện thực trong dự án | Vì sao dùng |
|-----------|----------------------|-------------|
| **Trừu tượng** | `Editable`, `Document` (abstract) | Che chi tiết triển khai; UI chỉ phụ thuộc hợp đồng |
| **Đóng gói** | Trường `private` + getter; `TextStatistics` bất biến; `FindReplaceService` | Bảo vệ trạng thái, dễ bảo trì/kiểm thử |
| **Kế thừa** | `TextDocument extends Document` | Tái sử dụng metadata tài liệu (title, file, modified) |
| **Đa hình** | Ghi đè `getContent`, `setContent`, `computeStatistics` | Có thể mở rộng (ví dụ `RichDocument`) mà không sửa UI |

## Luồng nghiệp vụ chính

1. `Main` → `MainFrame.launch()`
2. Mỗi tab = `EditorTabPanel` + `TextDocument`
3. Mở/Lưu qua `FileIOHelper`
4. Tìm–thay qua `FindReplaceDialog` + `FindReplaceService`
5. Thống kê qua `TextStatistics` → `JTable` + `ChartHelper` (JFreeChart)
6. Số dòng vẽ bằng `LineNumberPanel` (Graphics2D)

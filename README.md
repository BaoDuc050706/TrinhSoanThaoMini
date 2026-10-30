# Mini Text Editor — Trình soạn thảo văn bản Mini

Đồ án OOP: ứng dụng Swing nhiều cửa sổ, áp dụng 4 nguyên lý OOP, tích hợp JFreeChart.

## Tính năng

- Soạn thảo nhiều tab (`JTabbedPane`)
- Nút `×` trên từng tab; tài liệu có thay đổi sẽ hỏi lưu, bỏ thay đổi hoặc hủy đóng
- Nút `+` cạnh thanh tab để tạo tài liệu mới, kể cả khi không còn tab nào
- Mở / Lưu / Lưu thành (UTF-8)
- Tìm kiếm & thay thế (`JDialog`)
- Hoàn tác / Làm lại (`UndoManager`)
- Thống kê văn bản: `JTable` + biểu đồ cột/tròn (JFreeChart 1.5.x)
- Số dòng vẽ bằng **Graphics2D**
- Danh sách file gần đây (`JList`)
- Menu, toolbar, status bar

## Cấu trúc thư mục

```
TrinhSoanThaoMini/
├── src/editor/
│   ├── Main.java
│   ├── model/          # Nghiệp vụ (OOP)
│   ├── ui/             # Swing
│   └── util/           # File I/O, JFreeChart helper
├── lib/                # jfreechart-1.5.4.jar, jcommon-1.0.24.jar
├── docs/UML.md         # Sơ đồ lớp UML
└── README.md
```

## Yêu cầu

- JDK **17** (tối thiểu 11)
- Eclipse IDE (hoặc javac/java CLI)
- Thư viện trong `lib/` (đã kèm sẵn)

## Chạy trên Eclipse

1. File → **Import** → **Existing Projects into Workspace** → chọn thư mục dự án (không tick Copy).
2. Chuột phải project → **Refresh**.
3. **Project → Clean…** → chọn project → Clean.
4. Đóng các Run Configuration cũ (nếu có) rồi mở `src/editor/Main.java` → **Run As → Java Application**.

Main class: `editor.Main` trong module `trinhsoanthaomini`. Build Path phải có `lib/jfreechart-1.5.4.jar` (Is modular = yes).

Nếu vẫn báo `ClassNotFoundException: editor.Main`: Run → Run Configurations → xóa cấu hình Java Application cũ của project → chạy lại bước 4. Eclipse sẽ tạo cấu hình mới với `-m trinhsoanthaomini/editor.Main`.

## Chạy bằng dòng lệnh

```powershell
# Từ thư mục gốc dự án
javac -encoding UTF-8 -d bin -cp "lib/jfreechart-1.5.4.jar;lib/jcommon-1.0.24.jar" (Get-ChildItem -Recurse src\*.java).FullName
java -cp "bin;lib/jfreechart-1.5.4.jar;lib/jcommon-1.0.24.jar" editor.Main
```

## Phím tắt

| Phím | Chức năng |
|------|-----------|
| Ctrl+N | Tab mới |
| Ctrl+O | Mở file |
| Ctrl+S | Lưu |
| Ctrl+Shift+S | Lưu thành |
| Ctrl+W | Đóng tab |
| Ctrl+Z / Ctrl+Y | Hoàn tác / Làm lại |
| Ctrl+F | Tìm / Thay thế |
| Ctrl+T | Thống kê + biểu đồ |
| Ctrl+Q | Thoát |

## Nguyên lý OOP (tóm tắt thuyết trình)

1. **Trừu tượng** — `Editable`, `Document`: UI không phụ thuộc chi tiết lưu trữ.
2. **Đóng gói** — trạng thái `private`; `TextStatistics` immutable; service tách khỏi dialog.
3. **Kế thừa** — `TextDocument extends Document` tái sử dụng metadata file.
4. **Đa hình** — ghi đè `getContent` / `setContent` / `computeStatistics` để mở rộng loại tài liệu sau này.

Chi tiết sơ đồ lớp: [docs/UML.md](docs/UML.md).

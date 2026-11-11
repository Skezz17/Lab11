        }
    }

    private static String readField(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Поле не должно быть пустым.");
            } else if (value.contains(";") || value.contains("\n") || value.contains("\r")) {
                System.out.println("Символ ';' и переносы строк использовать нельзя.");
            } else {
                return value;
            }
        }
    }

    private static List<Book> readBooks() throws IOException {
        ensureFileExists();
        List<Book> books = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(";", -1);
                if (fields.length != 4) {
                    System.out.println("Пропущена некорректная запись в строке " + lineNumber + ".");
                    continue;
                }
                try {
                    int year = Integer.parseInt(fields[2]);
                    books.add(new Book(fields[0], fields[1], year, fields[3]));
                } catch (NumberFormatException e) {
                    System.out.println("Пропущена запись с некорректным годом в строке " + lineNumber + ".");
                }
            }
        }
        return books;
    }

    private static void showBooks() {
        try {
            List<Book> books = readBooks();
            System.out.println("\nСписок книг:");
            if (books.isEmpty()) {
                System.out.println("Библиотека пуста.");
                return;
            }
            for (int i = 0; i < books.size(); i++) {
                System.out.println((i + 1) + ". " + books.get(i));
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    private static void deleteBook() {
        try {
            List<Book> books = readBooks();
            if (books.isEmpty()) {
                System.out.println("Библиотека пуста. Удалять нечего.");
                return;
            }
            System.out.print("Введите точное название книги для удаления: ");
            String title = scanner.nextLine().trim();
            List<Book> matches = new ArrayList<>();
            for (Book book : books) {
                if (book.title.equalsIgnoreCase(title)) {
                    matches.add(book);
                }
            }
            if (matches.isEmpty()) {
                System.out.println("Книга с таким названием не найдена.");
                return;
            }

            System.out.println("Найдены совпадения: " + matches.size() + ".");
            System.out.print("Удалить все записи с этим названием? (да/нет): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("да")) {
                System.out.println("Удаление отменено.");
                return;
            }

            books.removeIf(book -> book.title.equalsIgnoreCase(title));
            writeBooks(books);
            System.out.println("Удалено записей: " + matches.size() + ".");
        } catch (IOException e) {
            System.out.println("Не удалось изменить файл: " + e.getMessage());
        }
    }

    private static void searchByAuthor() {
        try {
            List<Book> books = readBooks();
            System.out.print("Введите автора для поиска: ");
            String author = scanner.nextLine().trim();
            List<Book> matches = new ArrayList<>();
            for (Book book : books) {
                if (book.author.toLowerCase().contains(author.toLowerCase())) {
                    matches.add(book);
                }
            }
            if (matches.isEmpty()) {
                System.out.println("Книги этого автора не найдены.");
            } else {
                System.out.println("Результаты поиска:");
                for (Book book : matches) {
                    System.out.println(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл: " + e.getMessage());
        }
    }

    private static void writeBooks(List<Book> books) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Book book : books) {
            lines.add(book.toFileLine());
        }
        Files.write(FILE, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void ensureFileExists() throws IOException {
        if (FILE.getParent() != null) {
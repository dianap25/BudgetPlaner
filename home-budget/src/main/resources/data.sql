INSERT INTO category (name, monthly_limit) VALUES ('Jedzenie', 1200.00);
INSERT INTO category (name, monthly_limit) VALUES ('Transport', 300.00);
INSERT INTO category (name, monthly_limit) VALUES ('Rozrywka', 200.00);
INSERT INTO category (name, monthly_limit) VALUES ('Rachunki', 800.00);

INSERT INTO expense (category_id, amount, expense_date, description) VALUES (1, 640.00, '2027-02-05', 'Zakupy tygodniowe');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (4, 790.00, '2027-02-10', 'Prąd i gaz');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (1, 450.20, '2027-03-02', 'Zakupy tygodniowe');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (1, 380.00, '2027-03-09', 'Zakupy tygodniowe');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (1, 160.35, '2027-03-12', 'Restauracja');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (2, 120.00, '2027-03-01', 'Paliwo');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (2, 60.50, '2027-03-08', 'Taksówka');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (3, 150.00, '2027-03-06', 'Koncert');
INSERT INTO expense (category_id, amount, expense_date, description) VALUES (3, 75.99, '2027-03-13', 'Kino i kolacja');

INSERT INTO closed_month (month_key) VALUES ('2027-02');

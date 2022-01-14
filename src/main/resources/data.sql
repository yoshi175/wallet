INSERT INTO currency
    (id, name, code, symbol)
        VALUES
            (1, 'Euro', 'EUR', '€'),
            (2, 'British Pound', 'GBP', '£'),
            (3, 'US Dollar', 'USD', '$');

INSERT INTO balance
    (id, amount, currency_id)
        VALUES
            (1, 0, 1),
            (2, 0, 2),
            (3, 0, 3);

INSERT INTO player
    (id, first_name, last_name, email, balance_id)
        VALUES
            (1, 'Charlie', 'Anderson', 'charlie.andersson@mail.com', 1),
            (2, 'Cameron', 'Smith', 'cameron.smith@mail.com', 2),
            (3, 'Adrian', 'Walker', 'adrian.walker@mail.com', 3);

INSERT INTO transaction
    (id, transaction_id, balance, type, amount, `last`, player_id, currency_id, from_, `to`)
        VALUES
            (1, 'abc', 0, 'INITIAL', 0, true, 1, 1, CURRENT_TIMESTAMP, NULL),
            (2, 'def', 0, 'INITIAL', 0, true, 2, 2, CURRENT_TIMESTAMP, NULL),
            (3, 'ghi', 0, 'INITIAL', 0, true, 3, 3, CURRENT_TIMESTAMP, NULL);
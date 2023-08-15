package com.hmg.service;

import com.hmg.model.dto.AddExpenseDTO;
import com.hmg.model.entities.Expense;
import com.hmg.model.entities.Month;

public interface ExpenseService {
    Expense addExpenseToMonth(Month month, AddExpenseDTO addExpenseDTO);

    Expense getExpenseById(long expenseId);

    Expense editExpense(AddExpenseDTO addExpenseDTO, long expenseId);
}

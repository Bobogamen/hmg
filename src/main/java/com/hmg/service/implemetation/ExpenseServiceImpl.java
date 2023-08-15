package com.hmg.service.implemetation;

import com.hmg.model.dto.AddExpenseDTO;
import com.hmg.model.entities.Expense;
import com.hmg.model.entities.Month;
import com.hmg.repository.ExpenseRepository;
import com.hmg.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Expense addExpenseToMonth(Month month, AddExpenseDTO addExpenseDTO) {

        Expense expense = new Expense();
        expense.setMonth(month);
        expense.setAddedOn(LocalDate.now());
        expense.setName(addExpenseDTO.getName());
        expense.setValue(addExpenseDTO.getValue());
        expense.setDocumentNumber(addExpenseDTO.getDocumentNumber());

        if (addExpenseDTO.getDocumentDate() == null) {
            expense.setDocumentDate(LocalDate.now());
        } else {
            expense.setDocumentDate(addExpenseDTO.getDocumentDate());
        }

        return this.expenseRepository.save(expense);
    }

    @Override
    public Expense getExpenseById(long expenseId) {
        return this.expenseRepository.getExpenseById(expenseId);
    }

    @Override
    public Expense editExpense(AddExpenseDTO addExpenseDTO, long expenseId) {

        Expense expense = getExpenseById(expenseId);

        expense.setName(addExpenseDTO.getName());
        expense.setValue(addExpenseDTO.getValue());
        expense.setDocumentNumber(addExpenseDTO.getDocumentNumber());

        return this.expenseRepository.save(expense);
    }
}

'use strict';

const monthInput = document.getElementById('month');
const closedLabel = document.getElementById('closed-label');
const closeMonthButton = document.getElementById('close-month');
const messageElement = document.getElementById('message');
const cardsElement = document.getElementById('cards');
const totalsElement = document.getElementById('totals');
const expenseForm = document.getElementById('expense-form');
const expenseFields = document.getElementById('expense-fields');
const categorySelect = document.getElementById('expense-category');
const amountInput = document.getElementById('expense-amount');
const dateInput = document.getElementById('expense-date');
const descriptionInput = document.getElementById('expense-description');
const expensesBody = document.getElementById('expenses');
const noExpensesElement = document.getElementById('no-expenses');

let latestRefresh = 0;
let saving = false;

function formatMoney(value) {
	return Number(value).toFixed(2);
}

function formatPercent(value) {
	return value === null ? '—' : `${Number(value).toFixed(1)}%`;
}

function barWidth(category) {
	if (category.percentUsed === null) {
		return category.status === 'EXCEEDED' ? 100 : 0;
	}
	return Math.min(Number(category.percentUsed), 100);
}

function showMessage(text, kind) {
	messageElement.textContent = text;
	messageElement.className = `message message-${kind}`;
	messageElement.hidden = false;
}

function clearMessage() {
	messageElement.textContent = '';
	messageElement.hidden = true;
}

async function api(path, options) {
	let response;
	try {
		response = await fetch(path, options);
	} catch (error) {
		throw new Error('Brak połączenia z serwerem');
	}
	if (response.status === 204) {
		return null;
	}
	const body = await response.json().catch(() => null);
	if (!response.ok) {
		throw new Error(body && body.message ? body.message : `Serwer zwrócił błąd ${response.status}`);
	}
	return body;
}

function element(tag, className, text) {
	const created = document.createElement(tag);
	if (className) {
		created.className = className;
	}
	if (text !== undefined) {
		created.textContent = text;
	}
	return created;
}

function renderCard(category) {
	const card = element('article', `card status-${category.status}`);

	const header = element('div', 'card-header');
	header.append(element('h3', null, category.name));
	header.append(element('span', 'percent', formatPercent(category.percentUsed)));

	const bar = element('div', 'bar');
	const fill = element('div', 'bar-fill');
	fill.style.width = `${barWidth(category)}%`;
	bar.append(fill);

	card.append(header);
	card.append(element('p', 'spent', `${formatMoney(category.spent)} / ${formatMoney(category.limit)} zł`));
	card.append(bar);
	return card;
}

function renderSummary(summary) {
	cardsElement.replaceChildren(...summary.categories.map(renderCard));
	totalsElement.textContent = `Razem: wydano ${formatMoney(summary.totalSpent)} z ${formatMoney(summary.totalLimit)} zł, `
		+ `pozostało ${formatMoney(summary.totalRemaining)} zł`;
	closedLabel.hidden = !summary.closed;
	closeMonthButton.disabled = summary.closed;
	expenseFields.disabled = summary.closed;
}

function renderExpenseRow(expense, closed) {
	const row = document.createElement('tr');
	row.append(element('td', null, expense.date));
	row.append(element('td', null, expense.categoryName));
	row.append(element('td', null, expense.description === null ? '' : expense.description));
	row.append(element('td', 'amount', `${formatMoney(expense.amount)} zł`));

	const button = element('button', 'delete', 'Usuń');
	button.type = 'button';
	button.disabled = closed;
	button.addEventListener('click', () => deleteExpense(expense.id, button));
	const actions = element('td', 'actions');
	actions.append(button);
	row.append(actions);
	return row;
}

function renderExpenses(expenses, closed) {
	expensesBody.replaceChildren(...expenses.map((expense) => renderExpenseRow(expense, closed)));
	noExpensesElement.hidden = expenses.length > 0;
}

function renderUnavailable() {
	cardsElement.replaceChildren();
	totalsElement.textContent = '';
	expensesBody.replaceChildren();
	noExpensesElement.hidden = true;
	closedLabel.hidden = true;
	closeMonthButton.disabled = true;
	expenseFields.disabled = true;
}

async function refresh() {
	latestRefresh += 1;
	const current = latestRefresh;
	const month = encodeURIComponent(monthInput.value);
	try {
		const [summary, expenses] = await Promise.all([
			api(`/api/summary?month=${month}`),
			api(`/api/expenses?month=${month}`),
		]);
		if (current === latestRefresh) {
			renderSummary(summary);
			renderExpenses(expenses, summary.closed);
		}
	} catch (error) {
		if (current === latestRefresh) {
			renderUnavailable();
			showMessage(error.message, 'error');
		}
	}
}

async function loadCategories() {
	const categories = await api('/api/categories');
	categorySelect.replaceChildren(...categories.map((category) => {
		const option = element('option', null, category.name);
		option.value = category.id;
		return option;
	}));
}

async function addExpense(event) {
	event.preventDefault();
	if (saving) {
		return;
	}
	saving = true;
	try {
		const created = await api('/api/expenses', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({
				categoryId: categorySelect.value === '' ? null : Number(categorySelect.value),
				amount: amountInput.value === '' ? null : Number(amountInput.value),
				date: dateInput.value === '' ? null : dateInput.value,
				description: descriptionInput.value,
			}),
		});
		if (created.warning === null) {
			showMessage('Dodano wydatek', 'success');
		} else {
			showMessage(created.warning, created.status === 'EXCEEDED' ? 'error' : 'warning');
		}
		amountInput.value = '';
		descriptionInput.value = '';
		await refresh();
	} catch (error) {
		showMessage(error.message, 'error');
	} finally {
		saving = false;
	}
}

async function deleteExpense(id, button) {
	button.disabled = true;
	try {
		await api(`/api/expenses/${id}`, { method: 'DELETE' });
		showMessage('Usunięto wydatek', 'success');
	} catch (error) {
		showMessage(error.message, 'error');
	}
	await refresh();
}

async function closeMonth() {
	closeMonthButton.disabled = true;
	try {
		const closed = await api(`/api/months/${encodeURIComponent(monthInput.value)}/close`, { method: 'POST' });
		showMessage(`Zamknięto miesiąc ${closed.month}. Liczba wydatków: ${closed.expenseCount}, suma: ${formatMoney(closed.total)} zł`, 'success');
	} catch (error) {
		showMessage(error.message, 'error');
	}
	await refresh();
}

monthInput.addEventListener('change', () => {
	clearMessage();
	refresh();
});
closeMonthButton.addEventListener('click', closeMonth);
expenseForm.addEventListener('submit', addExpense);

loadCategories().catch((error) => showMessage(error.message, 'error'));
refresh();

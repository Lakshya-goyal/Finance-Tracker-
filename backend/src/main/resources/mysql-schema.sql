-- ==========================================================
-- Simple Beginner Full-Stack Finance Tracker Database Schema
-- Database: finance_tracker
-- ==========================================================

CREATE DATABASE IF NOT EXISTS finance_tracker;
USE finance_tracker;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL -- 'INCOME' or 'EXPENSE'
);

-- 3. Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'INCOME' or 'EXPENSE'
    description TEXT,
    transaction_date DATE NOT NULL,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Budgets Table
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Update admin user's password to 'password' (BCrypt encoded)
UPDATE users
SET password = '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6'
WHERE username = 'admin'; 
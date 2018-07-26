import os


class Config:
    CASE_DB_URI = os.getenv('CASE_DB_URI')
    COLLEX_DB_URI = os.getenv('COLLEX_DB_URI')

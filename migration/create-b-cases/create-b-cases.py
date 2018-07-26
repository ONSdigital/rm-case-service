import logging

from sqlalchemy import create_engine
from structlog import wrap_logger

from config import Config


logger = wrap_logger(logging.getLogger(__name__))


# Create missing B cases without actionplanid's
def create_b_cases():
    logger.info('Creating missing B cases')
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/create_b_cases.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', '')

    trans = connection.begin()
    connection.execute(sql)
    trans.commit()
    logging.info('Successfully created B cases')


# Retrieve the case uuid's and associated collectionexercise uuid's
# for the b cases missing their actionplanid's
def get_cases_without_actionplanids():
    logger.info("Retrieving B cases without actionplanid's")
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/create_b_cases.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', '')

    trans = connection.begin()
    response = connection.execute(sql)
    trans.commit()
    logging.info("Successfully retrieved B cases without actionplanid's")
    return [row for row in response]


def get_actionplanid(case_id, collection_exercise_id):
    logger.info('Retrieving actionplanid for case',
                case_id=case_id, collection_exercise_id=collection_exercise_id)
    engine = create_engine(Config.COLLEX_DB_URI)
    connection = engine.connect()

    with open('sql/get_actionplanid.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', '')
    sql.format(collection_exercise_id)

    trans = connection.begin()
    response = connection.execute(sql)
    trans.commit()
    logging.info('Successfully retrieved actionplanid',
                 case_id=case_id,
                 collection_exercise_id=collection_exercise_id,
                 actionplan_id=response.actionplanid)
    return response.actionplanid


def add_actionplanid(case_id, actionplan_id):
    logger.info('Updating case with actionplanid',
                case_id=case_id, actionplan_id=actionplan_id)
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/create_b_cases.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', '')
    sql.format(actionplan_id, case_id)

    trans = connection.begin()
    connection.execute(sql)
    trans.commit()
    logging.info('Successfully updated case with actionplanid',
                 case_id=case_id, actionplan_id=actionplan_id)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    create_b_cases()
    created_b_cases = get_cases_without_actionplanids()
    for case in created_b_cases:
        actionplan_id = get_actionplanid(case.id, case.collectionexerciseid)
        add_actionplanid(case.id, actionplan_id)
    logger.info('Successfully created new B cases', count=len(created_b_cases))

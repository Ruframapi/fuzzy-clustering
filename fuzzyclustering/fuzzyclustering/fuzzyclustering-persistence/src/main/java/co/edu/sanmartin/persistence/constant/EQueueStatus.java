package co.edu.sanmartin.persistence.constant;

/**
 * Enumeracion de los estados de la cola de procesos
 * @author Ricardo
 *
 */
public enum EQueueStatus {
	ENQUEUE,
	ACTIVE,
	CANCELLED,
	SUCESS,
	ERROR;
}

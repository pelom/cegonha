package br.pelommedrado.trans.download;
/**
	 * @author Andre Leite
	 */
	public class PacoteMal {

		/** Posicao inical **/
		private int off;

		/** Numero de bytes **/
		private int len;

		/**
		 * Construtor da classe.
		 * 
		 * @param off
		 * 		Posicao inicial
		 * 
		 * @param len
		 * 		Numero de bytes
		 */
		public PacoteMal(int off, int len) {
			super();

			this.off = off;
			this.len = len;
		}

		/**
		 * @return the off
		 */
		public int getOff() {
			return off;
		}

		/**
		 * @return the len
		 */
		public int getLen() {
			return len;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "PacoteMal [off=" + off + ", len=" + len + "]";
		}
	}